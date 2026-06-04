package com.foodieapp.notification.templates;
public class NotificationTemplates {
    public static String orderPlaced(Long orderId) {
        return "Order #" + orderId + " placed successfully! Waiting for restaurant confirmation.";
    }
    public static String orderConfirmed(Long orderId, String restaurant) {
        return "Great news! " + restaurant + " has confirmed your order #" + orderId + ".";
    }
    public static String orderOutForDelivery(Long orderId) {
        return "Your order #" + orderId + " is out for delivery. Track it live!";
    }
    public static String orderDelivered(Long orderId) {
        return "Order #" + orderId + " delivered! Enjoy your meal.";
    }
    public static String paymentSuccess(String amount) {
        return "Payment of INR " + amount + " was successful.";
    }
}
