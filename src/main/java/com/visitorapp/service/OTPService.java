package com.visitorapp.service;

import org.springframework.stereotype.Service;
import java.security.SecureRandom;

@Service
public class OTPService {

    private static final SecureRandom RANDOM = new SecureRandom();

    public String generateOTP() {
        int otp = 100000 + RANDOM.nextInt(900000);
        return String.valueOf(otp);
    }
}
