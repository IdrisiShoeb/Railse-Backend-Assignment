package com.railse.hiring.workforcemgmt.repository;

import com.railse.hiring.workforcemgmt.model.Activity;

import java.util.List;

public interface ActivityRepository {
    Activity save(Activity activity);
    List<Activity> findByTaskId(Long taskId);
}
