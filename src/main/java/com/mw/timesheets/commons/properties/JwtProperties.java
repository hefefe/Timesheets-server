package com.mw.timesheets.commons.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "timesheets.jwt")
public class JwtProperties {
    private Long accessTokenValidityTime;
    private String accessTokenSecret;
    private Long refreshTokenValidityTime;
    private String refreshTokenSecret;
    private String appName;
}
