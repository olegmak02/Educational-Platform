package com.oleg.educationalplatform.taskmodule.testmodule.questionanswer;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/test-answer")
@RequiredArgsConstructor
public class TestAnswerController {
    private final TestAnswerService service;

    @PostMapping("/submit")
    public ResponseEntity<String> submit(@RequestBody List<QuestionAnswer> answers) {
        service.submitAnswer(answers);
        return ResponseEntity.ok("Submitted result");
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<TestAnswer>> getTestAnswersByCourse(@PathVariable Integer courseId) {
        return ResponseEntity.ok(service.getTestAnswersByCourse(courseId));
    }

    @PostMapping("/create")
    public ResponseEntity<TestAnswer> create(@RequestBody TestAnswer testAnswer) {
        return ResponseEntity.ok(service.saveTestAnswer(testAnswer));
    }

    @GetMapping("/test/{testId}")
    public ResponseEntity<TestAnswer> findByTestId(@PathVariable Integer testId) {
        return ResponseEntity.ok(service.getAnswerByTestId(testId));
    }
}
