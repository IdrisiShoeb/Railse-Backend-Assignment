package com.railse.hiring.workforcemgmt.dto;

import lombok.Data;

@Data
public class TaskCommentRequest {
    private Long taskId;
    private Long userId;
    private String message;
}
