package com.example.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Request;
import org.apache.catalina.connector.Response;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "${frontend.url}", allowCredentials = "true")
public class ThirdPartyController {

    public static class ResendRequest {
        private String message;
        private String user;

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getUser() { return user; }
        public void setUser(String user) { this.user = user; }
    }

    @Value("${resend.api.key}")
    private String resendApiKey;

    @Value("${resend.owner.email}")
    private String ownerEmail;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/resend")
    public ResponseEntity<Map<String, Object>> sendEmail(@RequestBody ResendRequest request) {
        String url = "https://api.resend.com/emails";

        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(resendApiKey);

        // Prepare body
        Map<String, Object> body = new HashMap<>();
        body.put("from", "Job Poster <onboarding@resend.dev>");
        body.put("to", new String[]{ownerEmail});
        body.put("subject", "Customer review - " + request.getUser());
        body.put("html", "<p>" + request.getMessage() + "</p>");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            // Extract 'id' from the actual response
            Object id = response.getBody() != null ? response.getBody().get("id") : null;

            // Wrap it in the 'data' object to satisfy Postman tests
            Map<String, Object> wrappedResponse = new HashMap<>();
            wrappedResponse.put("data", Map.of("id", id));

            return ResponseEntity.ok(wrappedResponse);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error sending email: " + e.getMessage()));
        }
    }


    // ===================== AI Request =====================
    public static class AIPromptRequest {
        private String prompt;

        public String getPrompt() { return prompt; }
        public void setPrompt(String prompt) { this.prompt = prompt; }
    }

    @Value("${openai.api.key}")
    private String openAiApiKey;

    @PostMapping("/ai")
    public ResponseEntity<Map<String, Object>> generateResponsibilities(@RequestBody AIPromptRequest request) {
        try {
            String fullPrompt = "I will provide you with a job description. Extract exactly 5 key responsibilities of the role. "
                    + "Format the answer as a clean bullet-point list. Do not include requirements, skills, or benefits—only the main responsibilities. "
                    + request.getPrompt();

            String url = "https://api.openai.com/v1/responses";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(openAiApiKey);

            List<Map<String, String>> messages = List.of(
                    Map.of("role", "user", "content", fullPrompt)
            );

            Map<String, Object> requestBody = Map.of(
                    "model", "gpt-5",
                    "input", messages
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            Map<String, Object> body = response.getBody();

            String aiResponse = "";

            if (body != null && body.containsKey("output")) {
                List<Map<String, Object>> outputs = (List<Map<String, Object>>) body.get("output");
                for (Map<String, Object> outputItem : outputs) {
                    if ("message".equals(outputItem.get("type"))) {
                        List<Map<String, Object>> contentList = (List<Map<String, Object>>) outputItem.get("content");
                        if (contentList != null) {
                            for (Map<String, Object> contentItem : contentList) {
                                if ("output_text".equals(contentItem.get("type"))) {
                                    aiResponse = (String) contentItem.get("text");
                                    break;
                                }
                            }
                        }
                    }
                    if (!aiResponse.isEmpty()) break;
                }
            }

            // Return AIresponse at top level
            return ResponseEntity.ok(Map.of("AIresponse", aiResponse));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error generating AI response: " + e.getMessage()));
        }
    }



}
