package com.mw.timesheets.commons;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Getter
@Configuration
@ConfigurationProperties("timesheets.statistics")
public class StatisticsProperties {
    private Integer TimeInterval;
    private Double overtimePayRatio;
    private Double holidayPayRatio;
    private Integer workingDays;
}
