package com.booknest.orderservice.controller;

import com.booknest.orderservice.dto.OrderRequest;
import com.booknest.orderservice.dto.UpdateOrderStatusRequest;
import com.booknest.orderservice.model.Order;
import com.booknest.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/place")
    public Order placeOrder(@RequestBody OrderRequest request) {
        return orderService.placeOrder(request);
    }

    @GetMapping("/all")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{userId}")
    public List<Order> getUserOrders(@PathVariable String userId) {
        return orderService.getUserOrders(userId);
    }

    @PutMapping("/{id}/status")
    public Order updateOrderStatus(@PathVariable String id,
                                   @RequestBody UpdateOrderStatusRequest request) {
        return orderService.updateOrderStatus(id, request.getStatus());
    }

    @PutMapping("/cancel/{id}")
    public Order cancelOrder(@PathVariable String id) {
        return orderService.cancelOrder(id);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteOrder(@PathVariable String id) {
        orderService.deleteOrder(id);
    }
}