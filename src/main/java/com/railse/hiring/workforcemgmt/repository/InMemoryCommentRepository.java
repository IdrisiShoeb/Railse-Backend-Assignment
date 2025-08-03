package com.railse.hiring.workforcemgmt.repository;

import com.railse.hiring.workforcemgmt.model.Comment;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryCommentRepository implements CommentRepository {
    private final Map<Long, Comment> store = new ConcurrentHashMap<>();
    private final AtomicLong counter = new AtomicLong(1);

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == null) {
            comment.setId(counter.getAndIncrement());
        }
        store.put(comment.getId(), comment);
        return comment;
    }

    @Override
    public List<Comment> findByTaskId(Long taskId) {
        return store.values().stream()
                .filter(c -> c.getTaskId().equals(taskId))
                .sorted(Comparator.comparingLong(Comment::getTimestamp))
                .collect(Collectors.toList());
    }
}
