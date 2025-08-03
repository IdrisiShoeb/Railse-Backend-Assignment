# 🚀 Railse Backend Assignment

This Spring Boot project is a task management system for a logistics-style backend. It includes bug fixes and feature enhancements as per the assignment instructions.
This is an assignment by Railse for Backend Intern Role.
---

## 🐞 Bug Fixes

### 🐛 **Bug #1: Duplicate Open Tasks for Same Reference**

- **Problem**: Multiple open tasks were being allowed for the same reference ID and task type.
- **Fix**:
  - Cancel any existing open (non-completed) tasks of the same type.
  - Create a new task for reassignment.
  - Activity log tracks both the cancellation and new assignment.

✅ _Implemented in_: `assignByReference()` in `TaskManagementServiceImpl`  
✅ _Log Message_: `"Task cancelled during reassignment."`, `"Task reassigned to user X"`

---

### 🐛 **Bug #2: Cancelled Tasks Shown in Smart Daily View**

- **Problem**: Cancelled tasks were being returned in the `/fetch-by-date/v2` API.
- **Fix**:
  - Exclude all tasks with status `CANCELLED`.
  - Include:
    - Tasks due within the date range
    - Tasks due **before** start date but still open (not completed)

✅ _Implemented in_: `fetchTasksByDate()`  
✅ _Filter_: `status != CANCELLED && (inRange || overdue but not completed)`

---

## ✨ New Features

### ⭐ Feature 1: Smart Daily Task View (`/fetch-by-date/v2`)

- **Need**: Team leads want to see what’s due **today**, plus **overdue** tasks not yet done.
- **Implementation**:
  - Returns tasks where:
    - `deadline ∈ [startDate, endDate]` OR
    - `deadline < startDate && status != COMPLETED`
  - Excludes cancelled tasks.

✅ _Tested via_: `POST /fetch-by-date/v2`

---

### ⭐ Feature 2: Task Prioritization

- **Need**: Team leads want to set and filter by priority (LOW, MEDIUM, HIGH).
- **Implementation**:
  - Priority stored in each task.
  - New endpoint: `POST /update-priority`
  - Filter endpoint: `GET /priority/{priority}`
  - Logs activity when priority changes:
    - `"Priority changed from MEDIUM to HIGH"`

✅ _DTO_: `UpdatePriorityRequest`  
✅ _Activity Logging_: `logActivity()` method

---

### ⭐ Feature 3: Comments & Activity History

- **Need**: Users want to add comments to tasks and track everything that happened to a task.
- **Implementation**:
  - New models: `Comment`, `Activity`
  - Comments are free-text (no userId)
  - Activity log tracks:
    - Task creation
    - Priority/status/description changes
    - Reassignments
    - Comments
  - New endpoints:
    - `POST /comment` — add a comment
    - `GET /details/{taskId}` — full task view with comments and activity

✅ _Data returned_: `TaskDetailsDto`  
✅ _Auto-logging_: all major actions tracked in `ActivityRepository`

---

## 📦 Technologies Used

- Java 17
- Spring Boot
- In-memory storage (ConcurrentHashMap)
- Lombok
- MapStruct
- JSON-based APIs

---

## ✅ How to Test

Use Postman, curl, or Insomnia to test the following:

- ✅ Add/Update task
- ✅ Assign by reference
- ✅ Filter by priority
- ✅ Add comment
- ✅ Fetch task with full history

See `SUBMISSION.md`

---

## 📝 Author

**Idrisi Shoeb**  
Assignment for **Railse Backend Intern Role**

