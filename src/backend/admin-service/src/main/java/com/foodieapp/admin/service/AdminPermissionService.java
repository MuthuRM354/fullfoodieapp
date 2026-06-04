package com.foodieapp.admin.service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
@Service @Slf4j
public class AdminPermissionService {
    public List<String> getPermissions(String resource, String level) {
        return switch (level) {
            case "SUPER_ADMIN" -> List.of("READ", "WRITE", "DELETE", "ADMIN");
            case "ADMIN" -> List.of("READ", "WRITE", "DELETE");
            case "MODERATOR" -> List.of("READ", "WRITE");
            default -> List.of("READ");
        };
    }
    public boolean hasPermission(String adminLevel, String resource, String action) {
        List<String> perms = getPermissions(resource, adminLevel);
        return perms.contains(action);
    }
}
