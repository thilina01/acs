package com.thilina01.acs.smsservice.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thilina01.acs.smsservice.config.SmsProperties;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ReportEventListener {

    private final SmsProperties smsProps;
    private final RestTemplate restTemplate = new RestTemplate();

    public ReportEventListener(SmsProperties smsProps) {
        this.smsProps = smsProps;
    }

    @KafkaListener(topics = "report-generated", groupId = "sms-service")
    public void handle(String json) {
        System.out.println("🔔 Event received: " + json);

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);

            String message = root.path("message").asText();
            List<String> numbers = new ArrayList<>();
            root.path("numbers").forEach(n -> numbers.add(n.asText()));

            sendSms(message, numbers);
        } catch (Exception e) {
            System.err.println("❌ Error processing SMS payload: " + e.getMessage());
        }
    }

    private void sendSms(String message, List<String> numbers) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(smsProps.getToken());

        Map<String, Object> payload = Map.of(
                "message", message,
                "numbers", numbers
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    smsProps.getGatewayUrl(), entity, String.class);
            System.out.println("✅ SMS sent: " + response.getBody());
        } catch (Exception e) {
            System.err.println("❌ SMS send failed: " + e.getMessage());
        }
    }
}
