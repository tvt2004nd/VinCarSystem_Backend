package com.vin.VinSystem.Chat.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class OnlineStaffManager {

    private final Set<Long> onlineStaff = ConcurrentHashMap.newKeySet();

    public void staffOnline(Long staffId) {
        onlineStaff.add(staffId);
    }

    public void staffOffline(Long staffId) {
        onlineStaff.remove(staffId);
    }

    public Set<Long> getOnlineStaff() {
        return onlineStaff;
    }

}