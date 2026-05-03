package com.jobrecruitment.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "my.config")
public class JwtProperties {
    private String jwtSecret;
    private Long jwtExpiration;
    private String uploadPath;
}
