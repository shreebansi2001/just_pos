package com.crmportal.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.crmportal.service.TransliterationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class TransliterationServiceImpl implements TransliterationService {

    private final RestTemplate restTemplate = new RestTemplate();
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    

    private String callApi(String text, String langCode) {
        String url = String.format(
                "https://inputtools.google.com/request?text=%s&itc=%s-t-i0-und&num=1",
                text, langCode
        );

        List response = restTemplate.getForObject(url, List.class);
        if (response != null && !response.isEmpty()) {
            String status = (String) response.get(0);
            if ("SUCCESS".equals(status)) {
                List outerList = (List) response.get(1);
                if (outerList == null || outerList.isEmpty()) {
                    return text;
                }
                
                List innerList = (List) outerList.get(0);
                if (innerList == null || innerList.size() < 2) {
                    return text;
                }
                
                List candidates = (List) innerList.get(1);
                if (candidates == null || candidates.isEmpty()) {
                    return text;
                }
                
                return (String) candidates.get(0); // best candidate
            }
        }
        return text;
    }
    
    private String transliterateSentence(String text, String langCode) {
        StringBuilder result = new StringBuilder();

        Matcher matcher = Pattern.compile("[a-zA-Z]+|[^a-zA-Z]+").matcher(text);

        while (matcher.find()) {
            String token = matcher.group();

            if (token.matches("[a-zA-Z]+")) {
                result.append(callApi(token, langCode));
            } else {
                result.append(token); // punctuation / space 그대로 유지
            }
        }

        return result.toString();
    }


    

    @Override
    public String toHindi(String text) {
        return transliterateSentence(text, "hi");
    }

    @Override
    public String toGujarati(String text) {
        return transliterateSentence(text, "gu");
    }
    
    public Object translateAny(JsonNode inputNode) throws Exception {
        Map<String, Object> result = new LinkedHashMap<>();
        
        // Original data
        result.put("original", objectMapper.convertValue(inputNode, Object.class));
        
        // Convert to Hindi
        Object hindiTranslation = translateAnyToLanguage(inputNode, "hi");
        result.put("hindi", hindiTranslation);
        
        // Convert to Gujarati
        Object gujaratiTranslation = translateAnyToLanguage(inputNode, "gu");
        result.put("gujarati", gujaratiTranslation);
        
        return result;
    }

    public Object translateAnyToLanguage(JsonNode inputNode, String targetLanguage) throws Exception {
        if (inputNode.isArray()) {
            return translateJsonArray((ArrayNode) inputNode, targetLanguage);
        } else if (inputNode.isObject()) {
            return translateJsonObject((ObjectNode) inputNode, targetLanguage);
        } else if (inputNode.isTextual()) {
            return callApi(inputNode.asText(), targetLanguage);
        } else {
            // For numbers, booleans, null - return as is
            return objectMapper.convertValue(inputNode, Object.class);
        }
    }

    private Object translateJsonObject(ObjectNode objectNode, String targetLanguage) {
        Map<String, Object> result = new LinkedHashMap<>();
        
        objectNode.fields().forEachRemaining(entry -> {
            String key = entry.getKey();
            JsonNode value = entry.getValue();
            
            try {
                if (value.isTextual()) {
                    result.put(key, callApi(value.asText(), targetLanguage));
                } else if (value.isObject()) {
                    result.put(key, translateJsonObject((ObjectNode) value, targetLanguage));
                } else if (value.isArray()) {
                    result.put(key, translateJsonArray((ArrayNode) value, targetLanguage));
                } else {
                    // Numbers, booleans, null
                    result.put(key, objectMapper.convertValue(value, Object.class));
                }
            } catch (Exception e) {
                result.put(key, objectMapper.convertValue(value, Object.class));
            }
        });
        
        return result;
    }

    private List<Object> translateJsonArray(ArrayNode arrayNode, String targetLanguage) {
        List<Object> result = new ArrayList<>();
        
        arrayNode.forEach(item -> {
            try {
                if (item.isTextual()) {
                    result.add(callApi(item.asText(), targetLanguage));
                } else if (item.isObject()) {
                    result.add(translateJsonObject((ObjectNode) item, targetLanguage));
                } else if (item.isArray()) {
                    result.add(translateJsonArray((ArrayNode) item, targetLanguage));
                } else {
                    // Numbers, booleans, null
                    result.add(objectMapper.convertValue(item, Object.class));
                }
            } catch (Exception e) {
                result.add(objectMapper.convertValue(item, Object.class));
            }
        });
        
        return result;
    }

    // Legacy methods for backward compatibility
    public Map<String, Object> translateObject(Object inputObject) throws Exception {
        JsonNode jsonNode = objectMapper.valueToTree(inputObject);
        return (Map<String, Object>) translateAny(jsonNode);
    }

    public Object translateToLanguage(Object inputObject, String targetLanguage) throws Exception {
        JsonNode jsonNode = objectMapper.valueToTree(inputObject);
        return translateAnyToLanguage(jsonNode, targetLanguage);
    }

	@Override
	public String transliterate(String text, String lang) {
		return transliterateSentence(text, lang);
	}

	
}
