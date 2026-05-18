package com.booknest.orderservice.service;

import com.booknest.orderservice.dto.OrderRequest;
import com.booknest.orderservice.dto.OrderNotification;
import com.booknest.orderservice.model.OrderItem;
import com.booknest.orderservice.model.Order;
import com.booknest.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private NotificationProducer notificationProducer;

    private final RestClient restClient = RestClient.create("http://localhost:8080");

    public Order placeOrder(OrderRequest request) {
        if (request.getUserId() == null || request.getUserId().isBlank()) {
            throw new IllegalArgumentException("User ID is required");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        double totalAmount = 0;

        if (request.getItems() != null && !request.getItems().isEmpty()) {
            for (var itemRequest : request.getItems()) {
                if (itemRequest.getProductId() == null || itemRequest.getProductId().isBlank()) {
                    continue;
                }
                int qty = Math.max(1, itemRequest.getQuantity());
                Map product = validateAndAdjustStock(itemRequest.getProductId(), qty, false);
                String name = itemRequest.getProductName();
                if (name == null || name.isBlank()) {
                    name = String.valueOf(product.getOrDefault("name", "Book"));
                }
                double price = itemRequest.getPrice() > 0
                        ? itemRequest.getPrice()
                        : ((Number) product.getOrDefault("price", 0)).doubleValue();
                String imageUrl = itemRequest.getImageUrl() != null && !itemRequest.getImageUrl().isBlank()
                        ? itemRequest.getImageUrl()
                        : (String) product.get("imageUrl");

                OrderItem item = new OrderItem();
                item.setProductId(itemRequest.getProductId());
                item.setProductName(name);
                item.setPrice(price);
                item.setQuantity(qty);
                item.setImageUrl(imageUrl);
                orderItems.add(item);
                totalAmount += item.getPrice() * item.getQuantity();
            }
        } else if (request.getProductId() != null && !request.getProductId().isBlank()) {
            int qty = Math.max(1, request.getQuantity());
            Map product = validateAndAdjustStock(request.getProductId(), qty, false);
            String name = (request.getProductName() == null || request.getProductName().isBlank())
                    ? String.valueOf(product.getOrDefault("name", "Book"))
                    : request.getProductName();
            double price = request.getPrice() > 0
                    ? request.getPrice()
                    : ((Number) product.getOrDefault("price", 0)).doubleValue();
            String imageUrl = (String) product.get("imageUrl");

            OrderItem item = new OrderItem();
            item.setProductId(request.getProductId());
            item.setProductName(name);
            item.setPrice(price);
            item.setQuantity(qty);
            item.setImageUrl(imageUrl);
            orderItems.add(item);
            totalAmount = item.getPrice() * item.getQuantity();
        } else {
            throw new IllegalArgumentException("At least one order item is required");
        }

        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setUserName(request.getUserName());
        order.setUserEmail(request.getUserEmail());
        order.setPaymentMethod(request.getPaymentMethod() == null ? "COD" : request.getPaymentMethod());
        order.setItems(orderItems);
        order.setProductId(orderItems.get(0).getProductId());
        order.setProductName(orderItems.get(0).getProductName());
        order.setPrice(orderItems.get(0).getPrice());
        order.setQuantity(orderItems.get(0).getQuantity());
        order.setTotalAmount(request.getTotalAmount() > 0 ? request.getTotalAmount() : totalAmount);
        order.setPaymentId(request.getPaymentId());
        order.setStatus("PLACED");
        order.setRefundStatus("NOT_APPLICABLE");
        order.setOrderDate(LocalDateTime.now());

        Order saved = orderRepository.save(order);
        sendOrderNotifications(saved, "Order placed successfully.");
        return saved;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getUserOrders(String userId) {
        return orderRepository.findByUserId(userId);
    }

    public Order cancelOrder(String id) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            throw new IllegalArgumentException("Order Not Found");
        }
        if ("CANCELLED".equals(order.getStatus())) {
            return order;
        }
        if ("DELIVERED".equals(order.getStatus())) {
            throw new IllegalArgumentException("Delivered order cannot be cancelled");
        }

        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                if (item.getProductId() != null) {
                    validateAndAdjustStock(item.getProductId(), item.getQuantity(), true);
                }
            }
        }

        order.setStatus("CANCELLED");
        order.setCancelledAt(LocalDateTime.now());
        order.setRefundAmount(order.getTotalAmount());
        order.setRefundStatus("REFUNDED");
        Order saved = orderRepository.save(order);
        sendOrderNotifications(saved, "Order cancelled. Refund has been initiated successfully.");
        return saved;
    }

    public Order updateOrderStatus(String id, String status) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            throw new IllegalArgumentException("Order Not Found");
        }
        order.setStatus(status);
        Order saved = orderRepository.save(order);
        sendOrderNotifications(saved, "Order status updated to " + status + ".");
        return saved;
    }

    public void deleteOrder(String id) {
        if (!orderRepository.existsById(id)) {
            throw new IllegalArgumentException("Order Not Found");
        }
        orderRepository.deleteById(id);
    }

    private Map validateAndAdjustStock(String productId, int quantity, boolean restore) {
        Map product = restClient.get()
                .uri("/products/{id}", productId)
                .retrieve()
                .body(Map.class);

        int currentStock = ((Number) product.getOrDefault("stock", 0)).intValue();
        if (!restore && currentStock < quantity) {
            throw new IllegalArgumentException("Insufficient stock for product " + productId);
        }
        int targetStock = restore ? currentStock + quantity : currentStock - quantity;
        restClient.put()
                .uri("/products/stock/{id}?stock={stock}", productId, targetStock)
                .retrieve()
                .toBodilessEntity();
        return product;
    }

    private void sendOrderNotifications(Order order, String message) {
        OrderNotification customerNotification = new OrderNotification();
        customerNotification.setOrderId(order.getId());
        customerNotification.setUserEmail(order.getUserEmail());
        customerNotification.setRecipientType("CUSTOMER");
        customerNotification.setRecipientId(order.getUserId());
        customerNotification.setTitle("Order Update");
        customerNotification.setMessage(message);
        notificationProducer.sendNotification(customerNotification);

        OrderNotification adminNotification = new OrderNotification();
        adminNotification.setOrderId(order.getId());
        adminNotification.setRecipientType("ADMIN");
        adminNotification.setRecipientId("ADMIN");
        adminNotification.setTitle("New Order Activity");
        adminNotification.setMessage("Order " + order.getId() + " by user " + order.getUserId() + ": " + message);
        notificationProducer.sendNotification(adminNotification);
    }

}