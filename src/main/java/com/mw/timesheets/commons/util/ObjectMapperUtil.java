package com.mw.timesheets.commons.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ObjectMapperUtil {

    private final ObjectMapper objectMapper;

    @SneakyThrows
    public String toJSON(Object json) {
        return objectMapper.writeValueAsString(json);
    }
}
