package com.crmportal.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

@Service
public interface TransliterationService {

	String toHindi(String text);

	String toGujarati(String text);
	
	public Object translateAny(JsonNode inputObject) throws Exception;
	
	public Object translateAnyToLanguage(JsonNode inputNode, String targetLanguage) throws Exception;

	String transliterate(String text, String lang);
}
