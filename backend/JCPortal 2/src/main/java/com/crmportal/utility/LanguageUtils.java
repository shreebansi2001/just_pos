package com.crmportal.utility;

import java.util.List;
import java.util.Objects;

public class LanguageUtils {

	public static boolean containsHindi(List<String> list) {
		return list.stream().filter(Objects::nonNull).anyMatch(LanguageUtils::hasHindi);
	}

	public static boolean containsGujarati(List<String> list) {
		return list.stream().filter(Objects::nonNull).anyMatch(LanguageUtils::hasGujarati);
	}

	private static boolean hasHindi(String text) {
		for (char ch : text.toCharArray()) {
			if (isHindi(ch)) {
				return true;
			}
		}
		return false;
	}

	private static boolean hasGujarati(String text) {
		for (char ch : text.toCharArray()) {
			if (isGujarati(ch)) {
				return true;
			}
		}
		return false;
	}

	private static boolean isHindi(char ch) {
		return ch >= 0x0900 && ch <= 0x097F;
	}

	private static boolean isGujarati(char ch) {
		return ch >= 0x0A80 && ch <= 0x0AFF;
	}

	private static long countHindiOrGujarati(List<String> list) {
		return list.stream().filter(LanguageUtils::isHindiOrGujarati).count();
	}

	private static boolean isHindiOrGujarati(String text) {
		if (text == null)
			return false;
		for (char ch : text.toCharArray()) {
			if (isHindi(ch) || isGujarati(ch)) {
				return true;
			}
		}
		return false;
	}
}