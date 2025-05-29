package com.sammy.enterpriseresourceplanning.utils;

import java.security.SecureRandom;

/**
 * Utility class for generating unique codes for various employees in the system.
 */
public class CodeGenerator {

    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Generates a unique employee code with the format "EMP_" followed by 6 random alphanumeric characters.
     * 
     * @return A unique employee code
     */
    public static String generateEmployeeCode() {
        StringBuilder sb = new StringBuilder("EMP_");
        for (int i = 0; i < 6; i++) {
            int index = RANDOM.nextInt(ALPHANUMERIC.length());
            sb.append(ALPHANUMERIC.charAt(index));
        }
        return sb.toString();
    }
}