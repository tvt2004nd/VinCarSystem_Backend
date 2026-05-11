package com.vin.VinSystem.Config;

import com.vin.VinSystem.Common.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            if (!errors.containsKey(fe.getField())) {
                errors.put(fe.getField(), fe.getDefaultMessage());
            }
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Map<String, String>>builder()
                        .success(false)
                        .message("Dữ liệu không hợp lệ")
                        .data(errors)
                        .errorCode("VALIDATION_ERROR")
                        .timestamp(java.time.LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        String msg = ex.getMessage() != null ? ex.getMessage() : "Dữ liệu không hợp lệ";
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(msg, "VALIDATION_ERROR"));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadJson(HttpMessageNotReadableException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("JSON không hợp lệ", "BAD_JSON"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("Không có quyền truy cập", "FORBIDDEN"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadInput(IllegalArgumentException ex) {
        String msg = ex.getMessage() != null ? ex.getMessage() : "Dữ liệu không hợp lệ";
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(msg, "INVALID_INPUT"));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntime(RuntimeException ex) {
        String msg = ex.getMessage() != null ? ex.getMessage() : "Lỗi xử lý dữ liệu";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String code = "RUNTIME_ERROR";

        if (msg.contains("not found") || msg.contains("không tìm thấy")) {
            status = HttpStatus.NOT_FOUND;
            code = "NOT_FOUND";
        } else if (msg.contains("already exists") || msg.contains("đã tồn tại")) {
            status = HttpStatus.CONFLICT;
            code = "ALREADY_EXISTS";
        } else if (msg.contains("không đúng") || msg.contains("hết hạn") || msg.contains("Unauthorized")) {
            status = HttpStatus.UNAUTHORIZED;
            code = "UNAUTHORIZED";
        }

        return ResponseEntity
                .status(status)
                .body(ApiResponse.error(msg, code));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Lỗi hệ thống, vui lòng thử lại sau", "INTERNAL_SERVER_ERROR"));
    }
}