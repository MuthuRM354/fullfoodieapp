package com.foodieapp.order.service;

import com.foodieapp.order.model.*;
import com.foodieapp.order.repository.CartRepository;
import com.foodieapp.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartService cartService;
    private final OrderNotificationService notificationService;

    @Transactional
    public Order placeOrder(Long userId, String deliveryAddress, BigDecimal frontendTotal) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart is empty"));
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        List<OrderItem> orderItems = cart.getItems().stream()
                .map(ci -> OrderItem.builder()
                        .menuItemId(ci.getMenuItemId())
                        .name(ci.getName())
                        .price(ci.getPrice())
                        .quantity(ci.getQuantity())
                        .subtotal(ci.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())))
                        .build())
                .collect(Collectors.toList());

        // Use frontend total (includes delivery + tax) if provided; otherwise sum cart items
        BigDecimal total = (frontendTotal != null && frontendTotal.compareTo(BigDecimal.ZERO) > 0)
                ? frontendTotal
                : orderItems.stream()
                        .map(OrderItem::getSubtotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .userId(userId)
                .restaurantId(cart.getRestaurantId())
                .restaurantName(cart.getRestaurantName())
                .items(orderItems)
                .totalAmount(total)
                .deliveryAddress(deliveryAddress)
                .status(OrderStatus.PENDING)
                .paymentStatus("PENDING")
                .estimatedDeliveryTime("30-45 minutes")
                .build();

        order = orderRepository.save(order);
        cartService.clearCart(userId);

        // Notify user that order was placed (non-blocking — failure won't roll back the order)
        notificationService.notifyOrderPlaced(order);

        return order;
    }

    // Overload for backward compatibility (e.g. admin/internal calls)
    @Transactional
    public Order placeOrder(Long userId, String deliveryAddress) {
        return placeOrder(userId, deliveryAddress, null);
    }

    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = getOrder(orderId);
        order.setStatus(status);
        order = orderRepository.save(order);

        // Notify user of status change (non-blocking)
        notificationService.notifyStatusChange(order);

        return order;
    }

    public List<Order> getOrdersByUser(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Order> getOrdersByRestaurant(Long restaurantId) {
        return orderRepository.findByRestaurantIdOrderByCreatedAtDesc(restaurantId);
    }

    public List<Order> getOrderHistory(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED
                          || o.getStatus() == OrderStatus.CANCELLED)
                .collect(Collectors.toList());
    }
}
