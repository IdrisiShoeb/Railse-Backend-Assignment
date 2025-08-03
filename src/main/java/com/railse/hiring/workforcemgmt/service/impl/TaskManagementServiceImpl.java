package com.railse.hiring.workforcemgmt.service.impl;


import com.railse.hiring.workforcemgmt.common.exception.ResourceNotFoundException;
import com.railse.hiring.workforcemgmt.dto.*;
import com.railse.hiring.workforcemgmt.mapper.ITaskManagementMapper;
import com.railse.hiring.workforcemgmt.model.TaskManagement;
import com.railse.hiring.workforcemgmt.model.enums.Priority;
import com.railse.hiring.workforcemgmt.model.enums.Task;
import com.railse.hiring.workforcemgmt.model.enums.TaskStatus;
import com.railse.hiring.workforcemgmt.repository.TaskRepository;
import com.railse.hiring.workforcemgmt.service.TaskManagementService;
import org.springframework.stereotype.Service;
import com.railse.hiring.workforcemgmt.dto.*;
import com.railse.hiring.workforcemgmt.model.Comment;
import com.railse.hiring.workforcemgmt.model.Activity;
import com.railse.hiring.workforcemgmt.mapper.ICommentMapper;
import com.railse.hiring.workforcemgmt.mapper.IActivityMapper;
import com.railse.hiring.workforcemgmt.repository.CommentRepository;
import com.railse.hiring.workforcemgmt.repository.ActivityRepository;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class TaskManagementServiceImpl implements TaskManagementService {


    private final TaskRepository taskRepository;
    private final ITaskManagementMapper taskMapper;
    private final CommentRepository commentRepo;
    private final ActivityRepository activityRepo;
    private final ICommentMapper commentMapper;
    private final IActivityMapper activityMapper;


    public TaskManagementServiceImpl(
            TaskRepository taskRepository,
            ITaskManagementMapper taskMapper,
            CommentRepository commentRepo,
            ActivityRepository activityRepo,
            ICommentMapper commentMapper,
            IActivityMapper activityMapper
    ) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.commentRepo = commentRepo;
        this.activityRepo = activityRepo;
        this.commentMapper = commentMapper;
        this.activityMapper = activityMapper;
    }

    private void logActivity(Long taskId, String message) {
        Activity activity = new Activity();
        activity.setTaskId(taskId);
        activity.setDescription(message);
        activity.setTimestamp(System.currentTimeMillis());
        activityRepo.save(activity);
    }


    @Override
    public TaskManagementDto findTaskById(Long id) {
        TaskManagement task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        return taskMapper.modelToDto(task);
    }

    // Feature 2: Fetching all tasks of a specific Priority
    @Override
    public List<TaskManagementDto> fetchTasksByPriority(Priority priority) {
        List<TaskManagement> tasks = taskRepository.findAll().stream()
                .filter(task -> task.getPriority() == priority)
                .collect(Collectors.toList());

        return taskMapper.modelListToDtoList(tasks);
    }


    @Override
    public List<TaskManagementDto> updateTaskPriorities(UpdatePriorityRequest updateRequest) {
        List<TaskManagement> updatedTasks = new ArrayList<>();

        for (UpdatePriorityRequest.RequestItem item : updateRequest.getRequests()) {
            TaskManagement task = taskRepository.findById(item.getTaskId())
                    .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + item.getTaskId()));
            Priority oldPriority = task.getPriority();
            task.setPriority(item.getNewPriority());
            TaskManagement savedTask = taskRepository.save(task);
            // Log the priority change
            logActivity(savedTask.getId(), "Priority changed from " + oldPriority + " to " + item.getNewPriority());
            updatedTasks.add(savedTask);
        }

        return taskMapper.modelListToDtoList(updatedTasks);
    }



    @Override
    public List<TaskManagementDto> createTasks(TaskCreateRequest createRequest) {
        List<TaskManagement> createdTasks = new ArrayList<>();
        for (TaskCreateRequest.RequestItem item : createRequest.getRequests()) {
            TaskManagement newTask = new TaskManagement();
            newTask.setReferenceId(item.getReferenceId());
            newTask.setReferenceType(item.getReferenceType());
            newTask.setTask(item.getTask());
            newTask.setAssigneeId(item.getAssigneeId());
            newTask.setPriority(item.getPriority());
            newTask.setTaskDeadlineTime(item.getTaskDeadlineTime());
            newTask.setStatus(TaskStatus.ASSIGNED);
            newTask.setDescription("New task created.");
            //createdTasks.add(taskRepository.save(newTask));
            // Feature 3 implementation
            TaskManagement savedTask = taskRepository.save(newTask);
            logActivity(savedTask.getId(), "Task created and assigned to user " + item.getAssigneeId());
            createdTasks.add(savedTask);

        }
        return taskMapper.modelListToDtoList(createdTasks);
    }


    @Override
    public List<TaskManagementDto> updateTasks(UpdateTaskRequest updateRequest) {
        List<TaskManagement> updatedTasks = new ArrayList<>();
        if (updateRequest.getRequests() == null || updateRequest.getRequests().isEmpty()) {
            throw new IllegalArgumentException("Request list cannot be null or empty.");
        }
        for (UpdateTaskRequest.RequestItem item : updateRequest.getRequests()) {
            TaskManagement task = taskRepository.findById(item.getTaskId())
                    .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + item.getTaskId()));


            if (item.getTaskStatus() != null) {
                task.setStatus(item.getTaskStatus());
            }
            if (item.getDescription() != null) {
                task.setDescription(item.getDescription());
            }
            updatedTasks.add(taskRepository.save(task));
        }
        return taskMapper.modelListToDtoList(updatedTasks);
    }


//    @Override
//    public String assignByReference(AssignByReferenceRequest request) {
//        List<Task> applicableTasks = Task.getTasksByReferenceType(request.getReferenceType());
//        List<TaskManagement> existingTasks = taskRepository.findByReferenceIdAndReferenceType(request.getReferenceId(), request.getReferenceType());
//
//
//        for (Task taskType : applicableTasks) {
//            List<TaskManagement> tasksOfType = existingTasks.stream()
//                    .filter(t -> t.getTask() == taskType && t.getStatus() != TaskStatus.COMPLETED)
//                    .collect(Collectors.toList());
//
//
//            // BUG #1 is here. It should assign one and cancel the rest.
//            // Instead, it reassigns ALL of them.
//            if (!tasksOfType.isEmpty()) {
//                for (TaskManagement taskToUpdate : tasksOfType) {
//                    taskToUpdate.setAssigneeId(request.getAssigneeId());
//                    taskRepository.save(taskToUpdate);
//                }
//            } else {
//                // Create a new task if none exist
//                TaskManagement newTask = new TaskManagement();
//                newTask.setReferenceId(request.getReferenceId());
//                newTask.setReferenceType(request.getReferenceType());
//                newTask.setTask(taskType);
//                newTask.setAssigneeId(request.getAssigneeId());
//                newTask.setStatus(TaskStatus.ASSIGNED);
//                taskRepository.save(newTask);
//            }
//        }
//        return "Tasks assigned successfully for reference " + request.getReferenceId();
//    }


    @Override
    public String assignByReference(AssignByReferenceRequest request) {
       /*
       // BUG
        List<Task> applicableTasks = Task.getTasksByReferenceType(request.getReferenceType());
        List<TaskManagement> existingTasks = taskRepository.findByReferenceIdAndReferenceType(request.getReferenceId(), request.getReferenceType());


        for (Task taskType : applicableTasks) {
            List<TaskManagement> tasksOfType = existingTasks.stream()
                    .filter(t -> t.getTask() == taskType && t.getStatus() != TaskStatus.COMPLETED)
                    .collect(Collectors.toList());


            // BUG #1 is here. It should assign one and cancel the rest.
            // Instead, it reassigns ALL of them.
            if (!tasksOfType.isEmpty()) {
                for (TaskManagement taskToUpdate : tasksOfType) {
                    taskToUpdate.setAssigneeId(request.getAssigneeId());
                    taskRepository.save(taskToUpdate);
                }
            } else {
                // Create a new task if none exist
                TaskManagement newTask = new TaskManagement();
                newTask.setReferenceId(request.getReferenceId());
                newTask.setReferenceType(request.getReferenceType());
                newTask.setTask(taskType);
                newTask.setAssigneeId(request.getAssigneeId());
                newTask.setStatus(TaskStatus.ASSIGNED);
                taskRepository.save(newTask);
            }*/

        // Fix:
        // Step 1: Get all tasks applicable to the provided reference type
        List<Task> applicableTasks = Task.getTasksByReferenceType(request.getReferenceType());

        // Step 2: Fetch all existing tasks for this reference ID and reference type
        List<TaskManagement> existingTasks = taskRepository.findByReferenceIdAndReferenceType(
                request.getReferenceId(), request.getReferenceType());

        for (Task taskType : applicableTasks) {
            // Step 3: Filter existing tasks of the same type that are NOT completed
            List<TaskManagement> tasksOfType = existingTasks.stream()
                    .filter(t -> t.getTask() == taskType && t.getStatus() != TaskStatus.COMPLETED)
                    .collect(Collectors.toList());

            // Step 4: Cancel those existing tasks
            for (TaskManagement oldTask : tasksOfType) {
                oldTask.setStatus(TaskStatus.CANCELLED);
                oldTask.setDescription("Cancelled due to reassignment.");
                taskRepository.save(oldTask);
                // Feature 3 implementation
                logActivity(oldTask.getId(), "Task cancelled due to reassignment.");
            }

            // Step 5: Create a new task assigned to the new assignee
            TaskManagement newTask = new TaskManagement();
            newTask.setReferenceId(request.getReferenceId());
            newTask.setReferenceType(request.getReferenceType());
            newTask.setTask(taskType);
            newTask.setAssigneeId(request.getAssigneeId());
            newTask.setStatus(TaskStatus.ASSIGNED);
            newTask.setDescription("New task created via reassignment.");
            newTask.setTaskDeadlineTime(System.currentTimeMillis() + 86400000); // 1 day from now
            taskRepository.save(newTask);
            // Feature 3 implementation
            logActivity(newTask.getId(), "Task reassigned to user " + request.getAssigneeId());

        }

        return "Tasks reassigned successfully for reference " + request.getReferenceId();
    }


    @Override
    public List<TaskManagementDto> fetchTasksByDate(TaskFetchByDateRequest request) {
        List<TaskManagement> tasks = taskRepository.findByAssigneeIdIn(request.getAssigneeIds());


////         BUG #2 is here. It should filter out CANCELLED tasks but doesn't.
//        List<TaskManagement> filteredTasks = tasks.stream()
//                .filter(task -> {
//                    // This logic is incomplete for the assignment.
//                    // It should check against startDate and endDate.
//                    // For now, it just returns all tasks for the assignees.
//                    return true;
//                })
//                .collect(Collectors.toList());


//         Fix
//         Feature 1: A "Smart" Daily Task View
//         Enhanced the previous bug fix to implement the Feature
        List<TaskManagement> filteredTasks = tasks.stream()
                .filter(task -> task.getStatus() != TaskStatus.CANCELLED)
                .filter(task ->
                        // Include tasks within range
                        (task.getTaskDeadlineTime() >= request.getStartDate() && task.getTaskDeadlineTime() <= request.getEndDate()) ||
                        //  Include tasks started before the range that are still open
                        (task.getTaskDeadlineTime() < request.getStartDate() && task.getStatus() != TaskStatus.COMPLETED)
                )
                .collect(Collectors.toList());

        return taskMapper.modelListToDtoList(filteredTasks);
    }





    @Override
    public void addComment(TaskCommentRequest request) {
        // Validate task exists
        TaskManagement task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + request.getTaskId()));

        // Save comment
        Comment comment = new Comment();
        comment.setTaskId(request.getTaskId());
        comment.setUserId(request.getUserId());
        comment.setMessage(request.getMessage());
        comment.setTimestamp(System.currentTimeMillis());
        commentRepo.save(comment);

        // Log activity
        logActivity(request.getTaskId(), "User " + request.getUserId() + " commented: " + request.getMessage());
    }


    @Override
    public TaskDetailsDto getTaskDetails(Long id) {
        TaskManagement task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        TaskDetailsDto dto = new TaskDetailsDto();
        dto.setTask(taskMapper.modelToDto(task));
        dto.setComments(commentMapper.modelListToDtoList(commentRepo.findByTaskId(id)));
        dto.setActivityHistory(activityMapper.modelListToDtoList(activityRepo.findByTaskId(id)));

        return dto;
    }


}


