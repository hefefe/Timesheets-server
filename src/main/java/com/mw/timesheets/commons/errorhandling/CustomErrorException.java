package com.mw.timesheets.commons.errorhandling;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@AllArgsConstructor
@Getter
public class CustomErrorException extends RuntimeException {
    private List<? extends ErrorDTO> messages;

    private HttpStatus status;

    public CustomErrorException(String message, HttpStatus status) {
        messages = Lists.newArrayList(ErrorDTO.withSingleMessage(message));
        this.status = status;
    }
}
