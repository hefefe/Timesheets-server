package com.mw.timesheets.commons;


import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Getter
@ConfigurationProperties(prefix = "timesheets.jwt")
public class JwtProperties {
    private Long accessTokenEntityTime;
    private Long accessTokenSecret;
    private Long refreshTokenEntityTime;
    private Long refreshTokenSecret;
}
