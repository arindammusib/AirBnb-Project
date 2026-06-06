package com.AirBnd.AirBnB_backend.controller;

import com.AirBnd.AirBnB_backend.service.interfaces.BookingService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Webhook",
        description = "Handle webhook events for stripe payments")
public class WebhookController {

    private final BookingService bookingService;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @PostMapping("/payment")
    @Operation(summary = "Capture the payments",
            tags = {"Webhook"})
    public ResponseEntity<Void> capturePayments(
            HttpServletRequest request,  // ✅ CHANGED!
            @RequestHeader("Stripe-Signature")
            String sigHeader) {

        log.info("=== WEBHOOK HIT ===");

        try {
            // ✅ Read RAW body correctly
            String payload = StreamUtils.copyToString(
                    request.getInputStream(),
                    StandardCharsets.UTF_8
            );

            log.info("Payload length: {}", payload.length());
            log.info("Secret used: {}...",
                    endpointSecret.substring(0, 15));

            // ✅ Verify signature with raw payload
            Event event = Webhook.constructEvent(
                    payload,
                    sigHeader,
                    endpointSecret
            );

            log.info("✅ Event: {}", event.getType());

            try {
                bookingService.capturePayment(event);
                log.info("✅ capturePayment finished!");
            } catch (Exception e) {
                // Shows REAL error!
                log.error("❌ capturePayment FAILED: {}",
                        e.getMessage());
                log.error("Cause: ", e);
            }

            return ResponseEntity.ok().build(); // ✅ 200!

        } catch (SignatureVerificationException e) {
            log.error("❌ Signature failed: {}",
                    e.getMessage());
            return ResponseEntity.badRequest().build();

        } catch (IOException e) {
            log.error("❌ IO Error: {}", e.getMessage());
            return ResponseEntity.badRequest().build();

        } catch (Exception e) {
            log.error("❌ Error: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}