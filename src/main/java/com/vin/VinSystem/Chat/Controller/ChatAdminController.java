package com.vin.VinSystem.Chat.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.vin.VinSystem.Chat.Service.ChatAdminService;
import com.vin.VinSystem.Common.ApiResponse;

@RestController
@RequestMapping("/api/admin/chat")
public class ChatAdminController {

    @Autowired
    private ChatAdminService chatAdminService;

    /*
     =========================
     DASHBOARD
     =========================
     */

    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> stats() {
        return ApiResponse.success(chatAdminService.getStats());
    }

    @GetMapping("/daily")
    public ApiResponse<Map<String, Object>> daily() {
        return ApiResponse.success(chatAdminService.getDailyStats());
    }

    @GetMapping("/staff-stats")
    public ApiResponse<List<Map<String, Object>>> staffStats() {
        return ApiResponse.success(chatAdminService.getStaffStats());
    }

    /*
     =========================
     SESSIONS
     =========================
     */

    @GetMapping("/sessions")
    public ApiResponse<List<Map<String, Object>>> sessions() {
        return ApiResponse.success(chatAdminService.getAllSessions());
    }

    /*
     =========================
     READ MESSAGES
     =========================
     */

    @GetMapping("/session/{id}/messages")
    public ApiResponse<List<Map<String, Object>>> messages(@PathVariable Long id) {
        return ApiResponse.success(chatAdminService.getMessages(id));
    }

    /*
     =========================
     JOIN CHAT
     =========================
     */

    @PostMapping("/session/{id}/join")
    public ApiResponse<Map<String, Object>> join(
            @PathVariable Long id,
            @RequestParam Long staffId) {

        return ApiResponse.success(chatAdminService.joinSession(id, staffId), "Join session thành công");
    }

    /*
     =========================
     TRANSFER STAFF
     =========================
     */

    @PostMapping("/session/{id}/transfer")
    public ApiResponse<Map<String, Object>> transfer(
            @PathVariable Long id,
            @RequestParam Long staffId) {

        return ApiResponse.success(chatAdminService.transferSession(id, staffId), "Transfer session thành công");
    }

    /*
     =========================
     CLOSE SESSION
     =========================
     */

    @PostMapping("/session/{id}/close")
    public ApiResponse<Map<String, Object>> close(@PathVariable Long id) {

        return ApiResponse.success(chatAdminService.closeSession(id), "Close session thành công");
    }
}