package com.thilina01.acs.smsservice.listener;

import com.thilina01.acs.smsservice.config.SmsProperties;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.Map;

@Component
public class ReportEventListener {

    private final SmsProperties smsProps;

    public ReportEventListener(SmsProperties smsProps) {
        this.smsProps = smsProps;
    }

    @KafkaListener(topics = "report-generated", groupId = "sms-service")
    public void onReportGenerated(String messageJson) {
        System.out.println("🔔 Event received: " + messageJson);
        sendSms(smsProps.getMessage(), smsProps.getRecipients());
    }

    private void sendSms(String message, java.util.List<String> numbers) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(smsProps.getToken());

        Map<String, Object> payload = Map.of(
            "message", message,
            "numbers", numbers
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    smsProps.getGatewayUrl(), entity, String.class);
            System.out.println("✅ SMS sent: " + response.getBody());
        } catch (Exception e) {
            System.err.println("❌ Failed to send SMS: " + e.getMessage());
        }
    }
}
