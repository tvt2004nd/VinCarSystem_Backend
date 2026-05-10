package com.vin.VinSystem.Chat.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vin.VinSystem.Chat.Entity.ChatSession;
import com.vin.VinSystem.Chat.Repository.ChatSessionRepository;

@Service
public class StaffChatService {

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    public List<ChatSession> getActiveSessions() {

        return chatSessionRepository.findStaffSessions();
    }
}