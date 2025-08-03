package com.railse.hiring.workforcemgmt.dto;

import lombok.Data;

@Data
public class CommentDto {
    private Long id;
    private Long taskId;
    private Long userId;
    private String message;
    private Long timestamp;
}
