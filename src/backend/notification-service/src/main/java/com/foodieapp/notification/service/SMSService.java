package com.foodieapp.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

/**
 * Sends real SMS via Fast2SMS (https://www.fast2sms.com) — chosen because it
 * gives free credits on signup with no ongoing subscription, which fits a
 * demo/dev project. Uses the "q" (Quick) route, which sends plain
 * transactional-style text without requiring a DLT-registered sender ID.
 *
 * If FAST2SMS_API_KEY is not configured, this falls back to logging only so
 * the notification flow keeps working without failing.
 */
@Service
@Slf4j
public class SMSService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${fast2sms.api-key:}")
    private String apiKey;

    @Value("${fast2sms.api-url:https://www.fast2sms.com/dev/bulkV2}")
    private String apiUrl;

    @Value("${notification.sms.enabled:true}")
    private boolean enabled;

    private boolean isConfigured() {
        return enabled && StringUtils.hasText(apiKey);
    }

    public void sendSms(String phone, String message) {
        if (!StringUtils.hasText(phone)) {
            log.debug("Skipping SMS — no recipient phone number available");
            return;
        }
        if (!isConfigured()) {
            log.info("[SMS:log-only, FAST2SMS_API_KEY not set] Phone: {} | Message: {}", phone, message);
            return;
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("authorization", apiKey);
            headers.set("Content-Type", "application/x-www-form-urlencoded");

            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("route", "q");
            form.add("message", message);
            form.add("language", "english");
            form.add("flash", "0");
            form.add("numbers", normalize(phone));

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);
            ResponseEntity<String> resp = restTemplate.postForEntity(apiUrl, entity, String.class);

            if (resp.getStatusCode() == HttpStatus.OK) {
                log.info("SMS sent -> Phone: {}", phone);
            } else {
                log.warn("Fast2SMS returned non-200 status {} for phone {}", resp.getStatusCode(), phone);
            }
        } catch (Exception e) {
            // Non-fatal: notifications are still persisted to the DB even if
            // the SMS provider is down/misconfigured/out of credits.
            log.warn("Failed to send SMS to {}: {}", phone, e.getMessage());
        }
    }

    /** Fast2SMS expects bare 10-digit Indian mobile numbers, no country code / punctuation. */
    private String normalize(String phone) {
        String digits = phone.replaceAll("[^0-9]", "");
        if (digits.length() > 10) {
            digits = digits.substring(digits.length() - 10);
        }
        return digits;
    }

    public void sendOtp(String phone, String otp) {
        sendSms(phone, "Your FoodieApp OTP is: " + otp + ". Valid for 5 minutes.");
    }
}
