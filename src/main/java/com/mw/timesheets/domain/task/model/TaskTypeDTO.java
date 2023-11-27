package com.mw.timesheets.domain.task.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class TaskTypeDTO {

    private Long id;

    private String name;

    private String icon;

    private String color;
}
