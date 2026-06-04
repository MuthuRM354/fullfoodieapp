package com.foodieapp.notification.service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
@Service @Slf4j
public class SMSService {
    public void sendSms(String phone, String message) {
        log.info("SMS -> Phone: {} | Message: {}", phone, message);
    }
    public void sendOtp(String phone, String otp) {
        sendSms(phone, "Your FoodieApp OTP is: " + otp + ". Valid for 5 minutes.");
    }
}
