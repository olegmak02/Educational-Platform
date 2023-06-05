package com.oleg.educationalplatform.taskmodule.testmodule;

import com.oleg.educationalplatform.security.user.User;
import com.oleg.educationalplatform.taskmodule.testmodule.questionanswer.TestAnswer;
import com.oleg.educationalplatform.taskmodule.testmodule.questionanswer.TestAnswerRepository;
import com.oleg.educationalplatform.taskmodule.testmodule.questiontest.TestQuestionRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
@Data
public class TestController {
    private final TestRepository testRepository;
    private final TestQuestionRepository questionRepository;
    private final TestAnswerRepository testAnswerRepository;

    @GetMapping("/{id}")
    public ResponseEntity<Test> getTest(@PathVariable Integer id) {
        return ResponseEntity.ok(testRepository.findById(id).orElseThrow());
    }

    @PostMapping("/create")
    public ResponseEntity<Test> createTest(@RequestBody Test test) {
        return ResponseEntity.ok(testRepository.save(test));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Test>> getCourseTests(@PathVariable Integer courseId) {
        return ResponseEntity.ok(testRepository.findAllByCourseId(courseId));
    }

    @GetMapping("/incomplete/course/{courseId}")
    public ResponseEntity<List<Test>> getIncompleteTests(@PathVariable Integer courseId) {
        User student = (User) (SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        List<Test> tests = testRepository.findAllByCourseId(courseId);
        List<Integer> testAnswersIds = testAnswerRepository.findAnswersByCourse(student.getId(), courseId).stream().map(TestAnswer::getTestId).toList();
        return ResponseEntity.ok(tests.stream().filter(test -> new Date().before(test.getEndDate())).filter(test -> !testAnswersIds.contains(test.getId())).toList());
    }
}
