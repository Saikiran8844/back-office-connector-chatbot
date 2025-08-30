package com.chatbotservices.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chatbotservices.service.PaymentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * 
 * @author Saikiran Nannapanenni
 * 
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;
    
    @Operation(summary = "Capture a payment",
            description = "Captures a payment using a payment ID and amount. The payment ID is obtained from the Razorpay transaction.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment captured successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payment ID or amount"),
            @ApiResponse(responseCode = "402", description = "Payment required - Insufficient balance or failed transaction"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })

    @PostMapping("/capture")
    public ResponseEntity<String> capturePayment(@RequestParam String paymentId, @RequestParam int amount) {
        String response = paymentService.capturePayment(paymentId, amount);
        return ResponseEntity.ok(response);
    }
    
}
