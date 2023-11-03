package com.mw.timesheets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.DayOfWeek;
import java.time.YearMonth;

@SpringBootApplication
public class TimesheetsApplication {

    public static void main(String[] args) {
        SpringApplication.run(TimesheetsApplication.class, args);
    }

}
