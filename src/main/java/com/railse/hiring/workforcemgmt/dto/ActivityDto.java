package com.railse.hiring.workforcemgmt.dto;

import lombok.Data;

@Data
public class ActivityDto {
    private Long id;
    private Long taskId;
    private String description;
    private Long timestamp;
}
