package com.vin.VinSystem.Config;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ================= VALIDATION =================

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadInput(IllegalArgumentException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "message",
                        ex.getMessage() != null ? ex.getMessage() : "Dữ liệu không hợp lệ"
                ));
    }

    // ================= BUSINESS =================

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntime(RuntimeException ex) {

        String msg = ex.getMessage() != null ? ex.getMessage() : "";

        HttpStatus status = HttpStatus.BAD_REQUEST;

        if (msg.contains("not found") || msg.contains("không tìm thấy")) {

            status = HttpStatus.NOT_FOUND;

        } else if (msg.contains("already exists") || msg.contains("đã tồn tại")) {

            status = HttpStatus.CONFLICT;

        } else if (msg.contains("không đúng") || msg.contains("hết hạn")) {

            status = HttpStatus.UNAUTHORIZED;

        }

        return ResponseEntity
                .status(status)
                .body(Map.of(
                        "message",
                        msg.isEmpty() ? "Lỗi xử lý dữ liệu" : msg
                ));
    }

    // ================= SYSTEM =================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneral(Exception ex) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "message",
                        "Lỗi hệ thống, vui lòng thử lại sau"
                ));
    }
}