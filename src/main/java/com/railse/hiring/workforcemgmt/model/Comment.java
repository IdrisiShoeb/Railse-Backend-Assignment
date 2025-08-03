package com.railse.hiring.workforcemgmt.model;

import lombok.Data;

@Data
public class Comment {
    private Long id;
    private Long taskId;
    private Long userId;
    private String message;
    private Long timestamp;
}
