package com.mw.timesheets.domain.task.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.ByteArrayResource;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResourcesDTO {

    private byte[] resource;

    private String extension;
}
