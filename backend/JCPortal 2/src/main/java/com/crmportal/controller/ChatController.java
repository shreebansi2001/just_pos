package com.crmportal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.crmportal.request.dto.ChatRequest;
import com.crmportal.response.dto.ChatResponse;
import com.crmportal.service.impl.GeminiService;
import com.crmportal.service.impl.OpenAIService;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private OpenAIService openAIService;
    
    @Autowired
    private GeminiService geminiService;
    
    @PostMapping("/generate-gemini")
    public ResponseEntity<ChatResponse> generateGeminiResponse(@RequestBody ChatRequest request) {
        try {
            if (request.getUserPrompt() == null || request.getUserPrompt().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ChatResponse("User prompt is required", false));
            }

            String response = geminiService.generateResponse(
                request.getSystemPrompt(),
                request.getUserPrompt()
            );

            return ResponseEntity.ok(new ChatResponse(response));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new ChatResponse("Error: " + e.getMessage(), false));
        }
    }

    @PostMapping("/generate")
    public ResponseEntity<ChatResponse> generateResponse(@RequestBody ChatRequest request) {
        try {
            if (request.getUserPrompt() == null || request.getUserPrompt().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ChatResponse("User prompt is required", false));
            }

            String response = openAIService.generateResponse(
                request.getSystemPrompt(), 
                request.getUserPrompt()
            );
            
            return ResponseEntity.ok(new ChatResponse(response));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new ChatResponse("Error: " + e.getMessage(), false));
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("OpenAI integration is working!");
    }
}
