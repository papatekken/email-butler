package com.example.email_butler.controller;

import com.example.email_butler.model.ScanEstimate;
import com.example.email_butler.model.SenderCount;
import com.example.email_butler.model.SenderSize;
import com.example.email_butler.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/gmail")
public class GmailController {

    private final EmailService gmailService;

    public GmailController(EmailService gmailService) {
        this.gmailService = gmailService;
    }

    @GetMapping("/hello")
    public String getTopSenders(){
            return "hello from email butler";
    }

    @GetMapping("/countSummary")
    public ResponseEntity<?> getTopSenders(
            @RequestParam(defaultValue = "2147483647") int scanLimit) {
        try {
            List<SenderCount> result = gmailService.getTopSenders(scanLimit);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body("Error fetching count summary: " + e.getMessage());
        }
    }

    @GetMapping("/sizeSummary")
    public ResponseEntity<?> getSizeSummary(
            @RequestParam(defaultValue = "2147483647") int scanLimit) {
        try {
            List<SenderSize> result = gmailService.getTopSendersBySize(scanLimit);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body("Error fetching size summary: " + e.getMessage());
        }
    }

    @GetMapping("/estimate")
    public ResponseEntity<?> estimateScanTime() {
        try {
            ScanEstimate estimate = gmailService.estimateScanTime();
            return ResponseEntity.ok(estimate);
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body("Error estimating scan time: " + e.getMessage());
        }
    }


}
