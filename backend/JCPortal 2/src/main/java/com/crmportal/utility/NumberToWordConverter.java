package com.crmportal.utility;

public class NumberToWordConverter {

	private static final String[] belowTwenty = {
	        "", "One", "Two", "Three", "Four", "Five", "Six", "Seven",
	        "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen",
	        "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"
	};

	private static final String[] tens = {
	        "", "", "Twenty", "Thirty", "Forty", "Fifty",
	        "Sixty", "Seventy", "Eighty", "Ninety"
	};

	public static String convert(long num) {
	    if (num == 0) return "Zero";
	    return convertHelper(num).trim();
	}

	private static String convertHelper(long num) {

	    if (num < 20)
	        return belowTwenty[(int) num];

	    if (num < 100)
	        return tens[(int) num / 10] + " " + convertHelper(num % 10);

	    if (num < 1000)
	        return belowTwenty[(int) num / 100] + " Hundred " + convertHelper(num % 100);

	    if (num < 100000) // Thousand
	        return convertHelper(num / 1000) + " Thousand " + convertHelper(num % 1000);

	    if (num < 10000000) // Lakh
	        return convertHelper(num / 100000) + " Lakh " + convertHelper(num % 100000);

	    if (num < 1000000000) // Crore
	        return convertHelper(num / 10000000) + " Crore " + convertHelper(num % 10000000);

	    // Optional: Arab (for very large numbers)
	    return convertHelper(num / 1000000000) + " Arab " + convertHelper(num % 1000000000);
	}
}