package com.example.minion.util;

/**
 * Utility class for formatting phone numbers in the required format: 05X-XXXXXXX
 */
public final class PhoneFormatter {
    
    private static final String PHONE_PREFIX = "05";
    private static final String PHONE_FORMAT = "05%08d";
    private static final String SEPARATOR = "-";
    private static final int PREFIX_LENGTH = 3;
    
    private PhoneFormatter() {
        // Prevent instantiation
    }
    
    /**
     * Formats a long number into phone format: 05X-XXXXXXX
     * Example: 500000000 → "050-0000000"
     * 
     * @param number the number to format (must be non-negative)
     * @return formatted phone number
     * @throws IllegalArgumentException if number is negative
     */
    public static String formatPhone(long number) {
        if (number < 0) {
            throw new IllegalArgumentException("Phone number cannot be negative: " + number);
        }
        
        String phoneDigits = String.format(PHONE_FORMAT, number);
        return phoneDigits.substring(0, PREFIX_LENGTH) + SEPARATOR + phoneDigits.substring(PREFIX_LENGTH);
    }
}