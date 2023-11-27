package com.mw.timesheets.domain.task.model;

import com.mw.timesheets.domain.person.model.BasicPersonDataDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDTO {
    private BasicPersonDataDTO person;

    private List<CommentResourcesDTO> commentResources;

    private LocalDateTime postTime;

    private String commentContent;
}
