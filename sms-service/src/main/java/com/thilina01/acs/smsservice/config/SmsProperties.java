package com.thilina01.acs.smsservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "sms")
public class SmsProperties {
    private String gatewayUrl;
    private String token;
}
