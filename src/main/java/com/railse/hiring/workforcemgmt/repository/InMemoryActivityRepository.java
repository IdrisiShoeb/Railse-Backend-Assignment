package com.railse.hiring.workforcemgmt.repository;

import com.railse.hiring.workforcemgmt.model.Activity;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryActivityRepository implements ActivityRepository {
    private final Map<Long, Activity> store = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @Override
    public Activity save(Activity activity) {
        if (activity.getId() == null) {
            activity.setId(idCounter.getAndIncrement());
        }
        store.put(activity.getId(), activity);
        return activity;
    }

    @Override
    public List<Activity> findByTaskId(Long taskId) {
        return store.values().stream()
                .filter(a -> a.getTaskId().equals(taskId))
                .sorted(Comparator.comparingLong(Activity::getTimestamp))
                .collect(Collectors.toList());
    }
}
