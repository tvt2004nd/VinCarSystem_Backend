package com.vin.VinSystem.Chat.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.vin.VinSystem.Chat.Service.ChatAdminService;

@RestController
@RequestMapping("/api/admin/chat")
@CrossOrigin
public class ChatAdminController {

    @Autowired
    private ChatAdminService chatAdminService;

    /*
     =========================
     DASHBOARD
     =========================
     */

    @GetMapping("/stats")
    public Map<String, Object> stats() {
        return chatAdminService.getStats();
    }

    @GetMapping("/daily")
    public Map<String, Object> daily() {
        return chatAdminService.getDailyStats();
    }

    @GetMapping("/staff-stats")
    public List<Map<String, Object>> staffStats() {
        return chatAdminService.getStaffStats();
    }

    /*
     =========================
     SESSIONS
     =========================
     */

    @GetMapping("/sessions")
    public List<Map<String, Object>> sessions() {
        return chatAdminService.getAllSessions();
    }

    /*
     =========================
     READ MESSAGES
     =========================
     */

    @GetMapping("/session/{id}/messages")
    public List<Map<String, Object>> messages(@PathVariable Long id) {
        return chatAdminService.getMessages(id);
    }

    /*
     =========================
     JOIN CHAT
     =========================
     */

    @PostMapping("/session/{id}/join")
    public Map<String, Object> join(
            @PathVariable Long id,
            @RequestParam Long staffId) {

        return chatAdminService.joinSession(id, staffId);
    }

    /*
     =========================
     TRANSFER STAFF
     =========================
     */

    @PostMapping("/session/{id}/transfer")
    public Map<String, Object> transfer(
            @PathVariable Long id,
            @RequestParam Long staffId) {

        return chatAdminService.transferSession(id, staffId);
    }

    /*
     =========================
     CLOSE SESSION
     =========================
     */

    @PostMapping("/session/{id}/close")
    public Map<String, Object> close(@PathVariable Long id) {

        return chatAdminService.closeSession(id);
    }
}