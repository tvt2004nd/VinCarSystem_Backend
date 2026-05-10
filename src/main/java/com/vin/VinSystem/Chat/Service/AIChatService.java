package com.vin.VinSystem.Chat.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;   // ← THÊM
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AIChatService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String MODEL = "gemini-2.5-flash";

    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/"
            + MODEL + ":generateContent?key=";

    @Autowired
    private ConversationHistoryService historyService;

    @Autowired
    private CarContextService carContextService;

    // ✅ FIX 1: ObjectMapper cấu hình KHÔNG escape Unicode
    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(
                com.fasterxml.jackson.core.JsonGenerator.Feature.ESCAPE_NON_ASCII,
                false   
            );

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public String handleMessage(Long sessionId, String userMessage) {

        if (!historyService.hasHistory(sessionId)) {
            historyService.addMessage(sessionId, "system",
                    carContextService.buildSystemPrompt());
        }

        historyService.addMessage(sessionId, "user", userMessage);

        try {
            String systemPrompt = extractSystemPrompt(sessionId);
            List<Map<String, Object>> contents = buildContents(sessionId);

            Map<String, Object> requestMap = buildRequestBody(systemPrompt, contents);

            // ✅ FIX 2: Serialize JSON thành bytes UTF-8 tường minh
            byte[] requestBytes = objectMapper.writeValueAsBytes(requestMap);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(GEMINI_URL + apiKey))
                    .header("Content-Type", "application/json; charset=UTF-8")  // ← thêm charset
                    .POST(HttpRequest.BodyPublishers.ofByteArray(requestBytes))  // ← dùng bytes
                    .timeout(Duration.ofSeconds(30))
                    .build();

            // ✅ FIX 3: Đọc response body bằng UTF-8 tường minh
            HttpResponse<byte[]> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() != 200) {
                String errorBody = new String(response.body(), StandardCharsets.UTF_8);
                System.err.println("Gemini error " + response.statusCode() + ": " + errorBody);
                return "AI hiện đang bận. Vui lòng thử lại sau.";
            }

            // Parse response từ UTF-8 bytes
            String responseBody = new String(response.body(), StandardCharsets.UTF_8);
            JsonNode root = objectMapper.readTree(responseBody);
            String reply = root
                    .path("candidates").get(0)
                    .path("content")
                    .path("parts").get(0)
                    .path("text").asText();

            System.out.println("✅ Gemini reply: "
                    + reply.substring(0, Math.min(80, reply.length())) + "...");

            historyService.addMessage(sessionId, "assistant", reply);
            return reply;

        } catch (Exception e) {
            e.printStackTrace();
            return "AI đang gặp sự cố kỹ thuật. Vui lòng thử lại.";
        }
    }

    private String extractSystemPrompt(Long sessionId) {
        return historyService.getHistory(sessionId).stream()
                .filter(m -> "system".equals(m.get("role")))
                .map(m -> m.get("content"))
                .findFirst()
                .orElse("");
    }

    private List<Map<String, Object>> buildContents(Long sessionId) {
        List<Map<String, Object>> contents = new ArrayList<>();

        for (Map<String, String> msg : historyService.getHistory(sessionId)) {
            String role = msg.get("role");
            if ("system".equals(role)) continue;

            String geminiRole = "assistant".equals(role) ? "model" : "user";
            contents.add(Map.of(
                    "role", geminiRole,
                    "parts", List.of(Map.of("text", msg.get("content")))
            ));
        }

        return contents;
    }

    private Map<String, Object> buildRequestBody(
            String systemPrompt,
            List<Map<String, Object>> contents) {

        Map<String, Object> body = new java.util.LinkedHashMap<>();

        if (systemPrompt != null && !systemPrompt.isBlank()) {
            body.put("systemInstruction", Map.of(
                    "parts", List.of(Map.of("text", systemPrompt))
            ));
        }

        body.put("contents", contents);

        body.put("generationConfig", Map.of(
                "temperature", 0.7,
                "maxOutputTokens", 1024,
                "topP", 0.95,
                "topK", 40
        ));

        body.put("safetySettings", List.of(
                Map.of("category", "HARM_CATEGORY_HARASSMENT",        "threshold", "BLOCK_ONLY_HIGH"),
                Map.of("category", "HARM_CATEGORY_HATE_SPEECH",       "threshold", "BLOCK_ONLY_HIGH"),
                Map.of("category", "HARM_CATEGORY_SEXUALLY_EXPLICIT", "threshold", "BLOCK_ONLY_HIGH"),
                Map.of("category", "HARM_CATEGORY_DANGEROUS_CONTENT", "threshold", "BLOCK_ONLY_HIGH")
        ));

        return body;
    }

    public String handleMessage(String userMessage) {
        return handleMessage(0L, userMessage);
    }
}