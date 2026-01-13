package com.example.LuggageTracking.util;

import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Component
public class TrackingIdGenerator {
    
    private static final String PREFIX = "TRK";
    private static final Random random = new Random();
    
    public String generate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        int randomNum = random.nextInt(9999);
        return String.format("%s%s%04d", PREFIX, timestamp, randomNum);
    }
}