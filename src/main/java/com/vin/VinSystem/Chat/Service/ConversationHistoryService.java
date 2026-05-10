package com.vin.VinSystem.Chat.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class ConversationHistoryService {

    // sessionId -> list of {role, content}
    // role: "system" | "user" | "assistant"
    private final Map<Long, List<Map<String, String>>> histories = new ConcurrentHashMap<>();

    private static final int MAX_TURNS = 20;

    public List<Map<String, String>> getHistory(Long sessionId) {
        return histories.computeIfAbsent(sessionId, k -> new ArrayList<>());
    }

    public void addMessage(Long sessionId, String role, String content) {
        List<Map<String, String>> history = getHistory(sessionId);
        history.add(Map.of("role", role, "content", content));

        // Trim nếu quá dài — luôn giữ system prompt ở index 0
        if (history.size() > MAX_TURNS + 1) {
            List<Map<String, String>> trimmed = new ArrayList<>();
            trimmed.add(history.get(0)); // system prompt
            trimmed.addAll(history.subList(history.size() - MAX_TURNS, history.size()));
            histories.put(sessionId, trimmed);
        }
    }

    public boolean hasHistory(Long sessionId) {
        List<Map<String, String>> h = histories.get(sessionId);
        return h != null && !h.isEmpty();
    }

    public void clearHistory(Long sessionId) {
        histories.remove(sessionId);
    }
}