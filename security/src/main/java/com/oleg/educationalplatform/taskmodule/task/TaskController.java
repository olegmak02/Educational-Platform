package com.oleg.educationalplatform.taskmodule.task;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @GetMapping("/submitted/course/{courseId}")
    public ResponseEntity<List<Task>> getSubmittedTasks(@PathVariable("courseId") Integer courseId) {
        return ResponseEntity.ok(taskService.getSubmittedTaskByCourse(courseId));
    }

    @GetMapping("/incomplete/course/{courseId}")
    public ResponseEntity<List<Task>> getIncompleteTasks(@PathVariable("courseId") Integer courseId) {
        return ResponseEntity.ok(taskService.getIncompliteTaskByCourse(courseId));
    }

    @GetMapping("/course/{courseId}/title")
    public ResponseEntity<List<Task>> findByTitleAndCourse(@PathVariable("courseId") Integer courseId, @RequestParam("title") String title) {
        return ResponseEntity.ok(taskService.getTaskByCourseAndTitle(courseId, title));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Task>> getAllByCourseId(@PathVariable("courseId") Integer courseId) {
        return ResponseEntity.ok(taskService.getTaskByCourse(courseId));
    }

    @PostMapping("/create")
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        return ResponseEntity.ok(taskService.createTask(task));
    }
}
