package com.vin.VinSystem.Chat.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vin.VinSystem.Config.CloudinaryService;

@RestController
@RequestMapping("/api/upload")
public class ChatUploadController {

    private final CloudinaryService cloudinaryService;

    public ChatUploadController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    private static final long MAX_SIZE = 10L * 1024 * 1024; // 10 MB

    private static final Set<String> ALLOWED_MIME = Set.of(
        "image/jpeg", "image/png", "image/gif", "image/webp",
        "application/pdf",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "text/plain",
        "application/zip"
    );

    @PostMapping("/chat")
    public ResponseEntity<Map<String, Object>> upload(
            @RequestParam MultipartFile file) throws Exception {

        Map<String, Object> error = new HashMap<>();

        if (file.getSize() > MAX_SIZE) {
            error.put("error", "File không được vượt quá 10MB");
            return ResponseEntity.badRequest().body(error);
        }

        String mime = file.getContentType();
        if (mime == null || !ALLOWED_MIME.contains(mime)) {
            error.put("error", "Định dạng file không được hỗ trợ");
            return ResponseEntity.badRequest().body(error);
        }

        // Xác định folder theo loại file
        String folder = mime.startsWith("image/")
                ? "vinsystem/chat/images"
                : "vinsystem/chat/files";

        Map<String, Object> result = cloudinaryService.upload(file, folder);

        Map<String, Object> response = new HashMap<>();
        response.put("url",      result.get("secure_url"));
        response.put("fileName", file.getOriginalFilename());
        response.put("fileSize", file.getSize());
        response.put("fileMime", mime);

        return ResponseEntity.ok(response);
    }
}