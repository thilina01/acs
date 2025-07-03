package com.thilina01.acs.authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final StringRedisTemplate redisTemplate;

    private static final String OTP_PREFIX = "otp:";

    public String generateAndSaveOtp(String mobile) {
        String otp = String.valueOf(new Random().nextInt(900000) + 100000); // 6-digit
        redisTemplate.opsForValue().set(OTP_PREFIX + mobile, otp, 5, TimeUnit.MINUTES);
        return otp;
    }

    public boolean verifyOtp(String mobile, String otp) {
        String key = OTP_PREFIX + mobile;
        String cachedOtp = redisTemplate.opsForValue().get(key);
        return otp != null && otp.equals(cachedOtp);
    }

    public void invalidateOtp(String mobile) {
        redisTemplate.delete(OTP_PREFIX + mobile);
    }
}
