package com.mw.timesheets.commons.errorhandling;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ErrorDTO {

    private List<String> messages = Lists.newArrayList();

    public static ErrorDTO withSingleMessage(String message) {
        return ErrorDTO.builder()
                .messages(Lists.newArrayList(message))
                .build();
    }

    public static ErrorDTO withMultipleMessages(List<String> messages) {
        return ErrorDTO.builder()
                .messages(messages)
                .build();
    }
}
