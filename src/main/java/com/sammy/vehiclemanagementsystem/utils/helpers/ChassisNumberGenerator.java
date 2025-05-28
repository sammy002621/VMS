package com.sammy.vehiclemanagementsystem.utils.helpers;

import com.sammy.vehiclemanagementsystem.exceptions.ChassisNumberGenerationException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import java.security.SecureRandom;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class ChassisNumberGenerator {

    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int LENGTH = 12;
    private static final int MAX_ATTEMPTS = 10;

    private final Random random = new SecureRandom();

    public String generateChassisNumber() {
        StringBuilder sb = new StringBuilder("AN-"); // Optional prefix

        for (int i = 0; i < LENGTH; i++) {
            int index = random.nextInt(ALPHANUMERIC.length());
            sb.append(ALPHANUMERIC.charAt(index));
        }

        return sb.toString();
    }

    /**
     * Attempts to generate a unique chassis number, retries if the DB throws DataIntegrityViolationException.
     * Your service layer should call this and handle exceptions.
     */
    public String generateUniqueChassisNumberWithRetries(VehicleSaver vehicleSaver) {
        int attempts = 0;
        while (attempts < MAX_ATTEMPTS) {
            String chassisNumber = generateChassisNumber();
            try {
                vehicleSaver.saveWithChassisNumber(chassisNumber);
                return chassisNumber;
            } catch (DataIntegrityViolationException e) {
                attempts++;
            }
        }
        throw new ChassisNumberGenerationException("Failed to generate a unique chassis number after " + MAX_ATTEMPTS + " attempts");
    }

    @FunctionalInterface
    public interface VehicleSaver {
        void saveWithChassisNumber(String chassisNumber) throws DataIntegrityViolationException;
    }
}
