package io.invoice_system.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class AiService {
    
    private static final String START_URL = "http://localhost:1337/v1/models/start";
    private static final String CHAT_URL = "http://localhost:1337/v1/chat/completions";
    private RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=";

    public AiService() {
        this.restTemplate = new RestTemplate();
    }

    public String startModel(String model) {
    
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String startRequestBody = String.format("{\"model\": \"%s\"}", model);
        HttpEntity<String> startRequest = new HttpEntity<>(startRequestBody, headers);

        ResponseEntity<String> startResponse = restTemplate.exchange(START_URL, HttpMethod.POST, startRequest, String.class);
        if (startResponse.getStatusCode() != HttpStatus.OK) {
            return "Failed to start the model";
        }

        return "Model started successfully";
    }

    public String sendChat(String question, String model) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        String content = String.format("if we have table called invoices that have columns(user_id, id, total_amount,created_time, status) and want to: %s , just return the sql query without any explanations", question);

        String chatRequestBody = String.format("{\"messages\": [{\"role\": \"user\", \"content\": \"%s\", \"user\": \"Tala\"}], \"model\": \"%s\", \"stream\": false, \"max_tokens\": 200, \"stop\": [\"End\"]}", content, model);
        
        HttpEntity<String> chatRequest = new HttpEntity<>(chatRequestBody, headers);
        ResponseEntity<String> chatResponse = restTemplate.exchange(CHAT_URL, HttpMethod.POST, chatRequest, String.class);
        System.out.println(chatResponse);
        if (chatResponse.getStatusCode() != HttpStatus.OK) {
        	 System.out.println("failed");
            return "Failed to get chat response";
        }
        return chatResponse.getBody();
    }

  
    public String getJanAIResponse(String question) {
    	
        String model = "llamacorn-1.1b";
       
       // String startResponse = startModel(model);
        
        /*if (!startResponse.contains("Model started successfully")) {
            System.out.println("Error starting model: " + startResponse);
            return "Error starting model: " + startResponse;
        }*/
      //  System.out.println("hi tala");
        
        return sendChat(question, model);
    }
    
    
    public String getGeminiResponse(String question) {
        String content = String.format("if we have table called invoices that have columns(user_id, id, total_amount,created_time, status) and want to: %s, just return the sql query without any explanations", question);
      
        
        String fullApiUrl = API_URL + apiKey;
        String requestBody = """
                {
                  "contents": [ {
                    "parts": [{"text": "%s"}]
                  }]
                }
                """.formatted(content);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        
        ResponseEntity<Map> response = restTemplate.exchange(fullApiUrl, HttpMethod.POST, request, Map.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = response.getBody();
            System.out.println("hi" + response.getBody());
            if (responseBody != null && responseBody.containsKey("candidates")) {
                Map<String, Object> candidate = (Map<String, Object>) ((java.util.List) responseBody.get("candidates")).get(0);
                Map<String, Object> contentResponse = (Map<String, Object>) candidate.get("content");

                if (contentResponse != null && contentResponse.containsKey("parts")) {
                    Map<String, Object> part = (Map<String, Object>) ((java.util.List) contentResponse.get("parts")).get(0);
                    if (part != null && part.containsKey("text")) {
                        return (String) part.get("text");
                    }
                }
            }
            return "No candidates or content found.";
        } else {
            return "Error: " + response.getStatusCode();
        }
    }

    }
    

