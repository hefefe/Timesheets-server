package com.mw.timesheets.commons.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Data
@Configuration
@ConfigurationProperties("timesheets.statistics")
public class StatisticsProperties {
    private Integer TimeInterval;
    private Double overtimePayRatio;
    private Double holidayPayRatio;
    private Integer workingDays;
}
