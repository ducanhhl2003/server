package book.service.impl;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class GeminiService {
    private static final String API_KEY = "AIzaSyCUm2iSwxOdpwylHZ6CLOh2iGERJl7a18c"; // Thay bằng API Key thật
    private static final String URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;

    public String askGemini(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Build request body
        Map<String, Object> request = new HashMap<>();
        Map<String, Object> part = Map.of("text", prompt);
        Map<String, Object> content = Map.of("parts", List.of(part));
        request.put("contents", List.of(content));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(URL, entity, Map.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                List<Map> candidates = (List<Map>) response.getBody().get("candidates");
                if (candidates == null || candidates.isEmpty()) return "Không có phản hồi";

                Map contentResult = (Map) candidates.get(0).get("content");
                List<Map> parts = (List<Map>) contentResult.get("parts");
                return parts != null && !parts.isEmpty() ? (String) parts.get(0).get("text") : "Không có phản hồi";
            } else {
                return "Đã xảy ra lỗi: " + response.getStatusCodeValue();
            }
        } catch (Exception e) {
            return "Đã xảy ra lỗi: " + e.getMessage();
        }
    }
}
