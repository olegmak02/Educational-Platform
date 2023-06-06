package com.oleg.educationalplatform.taskmodule.taskanswer;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/answer")
@RequiredArgsConstructor
public class TaskAnswerController {

    private final TaskAnswerService taskAnswerService;

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<TaskAnswer>> getAnswersByTaskId(@PathVariable("taskId") Integer taskId) {
        return ResponseEntity.ok(taskAnswerService.getAnswersByTaskId(taskId));
    }

    @GetMapping("/task/{taskId}/my-answer")
    public ResponseEntity<TaskAnswer> getUsersAnswer(@PathVariable("taskId") Integer taskId) {
        TaskAnswer res = taskAnswerService.getAnswerByTaskIdAndCurrentUser(taskId);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/task/{taskId}/student/{studentId}")
    public ResponseEntity<TaskAnswer> getUsersAnswerByTask(@PathVariable("taskId") Integer taskId, @PathVariable("studentId") Integer studentId) {
        TaskAnswer res = taskAnswerService.getAnswerByTaskIdAndUser(taskId, studentId);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/create")
    public ResponseEntity<TaskAnswer> createTaskAnswer(@RequestBody TaskAnswer task) {
        return ResponseEntity.ok(taskAnswerService.createTaskAnswer(task));
    }
}
