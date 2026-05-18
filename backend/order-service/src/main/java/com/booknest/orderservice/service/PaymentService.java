package com.booknest.orderservice.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    @Value("${razorpay.api.key}")
    private String razorpayApiKey;

    @Value("${razorpay.api.secret}")
    private String razorpayApiSecret;

    public Map<String, String> createRazorpayOrder(double amount) throws RazorpayException {
        RazorpayClient razorpayClient = new RazorpayClient(razorpayApiKey, razorpayApiSecret);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", (int) (amount * 100)); // amount in paise
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "txn_" + System.currentTimeMillis());

        Order razorpayOrder = razorpayClient.orders.create(orderRequest);

        Map<String, String> response = new HashMap<>();
        response.put("orderId", razorpayOrder.get("id"));
        response.put("amount", String.valueOf(amount));
        response.put("currency", "INR");

        return response;
    }
}
