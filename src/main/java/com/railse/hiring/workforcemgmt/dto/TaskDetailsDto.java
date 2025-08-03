package com.railse.hiring.workforcemgmt.dto;

import lombok.Data;

import java.util.List;

@Data
public class TaskDetailsDto {
    private TaskManagementDto task;
    private List<CommentDto> comments;
    private List<ActivityDto> activityHistory;
}
