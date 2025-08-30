package com.chatbotservices.service;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
/**
 * 
 * @author Saikiran Nannapanenni
 * 
 */
@Service
public class PaymentService {

    @Value("${razorpay.api.key}")
    private String razorpayKey;

    @Value("${razorpay.api.secret}")
    private String razorpaySecret;

    public String capturePayment(String paymentId, int amount) {
        try {
            // Initialize Razorpay client
            RazorpayClient razorpay = new RazorpayClient(razorpayKey, razorpaySecret);

            // Create the request JSON object
            JSONObject paymentRequest = new JSONObject();
            paymentRequest.put("amount", amount * 100); 
            paymentRequest.put("currency", "INR");

            // Capture the payment
            Payment payment = razorpay.Payments.capture(paymentId, paymentRequest);

            JSONObject response = new JSONObject();
            response.put("status", "success");
            response.put("message", "Payment captured successfully");

            return response.toString();
        } catch (Exception e) {
            JSONObject response = new JSONObject();
            response.put("status", "failure");
            response.put("message", e.getMessage());

            return response.toString();
        }
    }

}
