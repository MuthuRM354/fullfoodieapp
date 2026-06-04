package com.foodieapp.notification.templates;
public class EmailTemplates {
    public static String orderConfirmed(Long orderId, String restaurantName) {
        return "Hello! Your order #" + orderId + " from " + restaurantName + " has been confirmed. We'll notify you as it progresses.";
    }
    public static String orderDelivered(Long orderId) {
        return "Your order #" + orderId + " has been delivered. Enjoy your meal! Rate your experience on FoodieApp.";
    }
    public static String paymentSuccess(String amount, Long orderId) {
        return "Payment of INR " + amount + " for order #" + orderId + " was successful. Transaction ID saved.";
    }
    public static String paymentFailed(Long orderId) {
        return "Payment for order #" + orderId + " failed. Please retry or use a different payment method.";
    }
}
