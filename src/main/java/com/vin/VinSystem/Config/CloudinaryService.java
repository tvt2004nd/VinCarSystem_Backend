package com.vin.VinSystem.Config;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CloudinaryService {

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    // ── Upload ảnh ────────────────────────────────────────────────────────
    public Map<String, Object> upload(MultipartFile file, String folder) throws IOException {
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);

        // Cloudinary signature: sort params alphabet, concat apiSecret SAU CÙNG (không có &)
        // folder=vinsystem/avatars&timestamp=1234567890<apiSecret>
        String toSign   = "folder=" + folder + "&timestamp=" + timestamp + apiSecret;
        String signature;
        try {
            signature = sha1Hex(toSign);
        } catch (Exception e) {
            throw new IOException("Lỗi tạo signature", e);
        }

        System.out.println(">>> Cloudinary upload - folder: " + folder);
        System.out.println(">>> toSign: " + toSign);
        System.out.println(">>> signature: " + signature);

       String url = "https://api.cloudinary.com/v1_1/" + cloudName + "/auto/upload"; // Đã sửa thành "auto"

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });
        body.add("api_key",   apiKey);
        body.add("timestamp", timestamp);
        body.add("folder",    folder);
        body.add("signature", signature);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            System.out.println(">>> Cloudinary response: " + response.getBody());
            return response.getBody();
        } catch (Exception e) {
            System.err.println(">>> Cloudinary error: " + e.getMessage());
            throw new IOException("Upload Cloudinary thất bại: " + e.getMessage(), e);
        }
    }

    // ── Xóa ảnh ───────────────────────────────────────────────────────────
    public void delete(String publicId) {
        try {
            String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
            String toSign    = "public_id=" + publicId + "&timestamp=" + timestamp + apiSecret;
            String signature = sha1Hex(toSign);

            String url = "https://api.cloudinary.com/v1_1/" + cloudName + "/image/destroy";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("public_id", publicId);
            body.add("api_key",   apiKey);
            body.add("timestamp", timestamp);
            body.add("signature", signature);

            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
            restTemplate.postForEntity(url, request, Map.class);
        } catch (Exception e) {
            System.err.println("Lỗi xóa ảnh Cloudinary: " + e.getMessage());
        }
    }

    // ── SHA1 ───────────────────────────────────────────────────────────────
    private String sha1Hex(String input) throws Exception {
        MessageDigest md   = MessageDigest.getInstance("SHA-1");
        byte[]        hash = md.digest(input.getBytes("UTF-8"));
        StringBuilder sb   = new StringBuilder();
        for (byte b : hash) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}