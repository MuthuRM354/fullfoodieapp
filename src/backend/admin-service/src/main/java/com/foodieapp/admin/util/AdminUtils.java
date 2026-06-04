package com.foodieapp.admin.util;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class AdminUtils {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static String formatDateTime(LocalDateTime dt) { return dt != null ? dt.format(FORMATTER) : ""; }
    public static String buildAuditDetails(String action, String entity, Object data) {
        return String.format("[%s] %s: %s", action, entity, data);
    }
    private AdminUtils() {}
}
