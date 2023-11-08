package com.mw.timesheets.domain.statistcs.model.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class DataListDTO {

    private List<? extends CommittedAndUncommittedDTO> data;

}
