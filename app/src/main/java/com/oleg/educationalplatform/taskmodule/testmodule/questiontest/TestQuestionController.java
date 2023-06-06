package com.oleg.educationalplatform.taskmodule.testmodule.questiontest;

import com.oleg.educationalplatform.taskmodule.attachment.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/question")
@RequiredArgsConstructor
public class TestQuestionController {

    private final TestQuestionRepository testQuestionRepository;
    private final AttachmentRepository attachmentRepository;

    @PostMapping("/create")
    public ResponseEntity<TestQuestion> createTestQuestion(@RequestBody TestQuestion test) {
        return ResponseEntity.ok(testQuestionRepository.save(test));
    }

    @GetMapping("/test/{testId}")
    public ResponseEntity<List<TestQuestion>> getQuestionsByTest(@PathVariable("testId") Integer testId) {
        List<TestQuestion> questions = testQuestionRepository.findByTestId(testId);
        for (TestQuestion question: questions) {
            question.setAttachments(attachmentRepository.getIdsByPostId(question.getId()));
            question.setCorrect(null);
        }
        return ResponseEntity.ok(questions);
    }
}
