package book.controller;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import book.service.impl.GeminiService;

@RestController
@RequestMapping("/gemini")
public class GeminiController {

    @Autowired
    private GeminiService geminiService;

//    @PostMapping("/ask")
//    public GeminiResponse ask(@RequestBody GeminiRequest request) {
//        String answer = geminiService.askGemini(request.getPrompt());
//        return new GeminiResponse(answer);
//    }
    @PostMapping("/ask")
    public ResponseEntity<String> askGemini(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        String answer = geminiService.askGemini(prompt);
        return ResponseEntity.ok(answer);
    }

}
