package com.oleg.educationalplatform.assessment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/assessment")
public class AssessmentController {

    private final AssessmentService assessmentService;

    @GetMapping("/my-assessments")
    public ResponseEntity<List<Assessment>> getAllAssessmentsByCourse(@RequestParam("courseId") Integer courseId) {
        return ResponseEntity.ok(assessmentService.getAssessmentsByCourseId(courseId));
    }

    @PostMapping("/update-mark")
    public ResponseEntity<Assessment> updateMarkAssessment(@RequestBody Assessment assessment) {
        return ResponseEntity.ok(assessmentService.updateMarkAssessment(assessment));
    }

    @PostMapping("/create")
    public ResponseEntity<Assessment> createAssessment(@RequestBody Assessment assessment) {
        return ResponseEntity.ok(assessmentService.createAssessment(assessment));
    }

    @GetMapping("/course/{courseId}/student/{studentId}")
    public ResponseEntity<List<Assessment>> getStudentAssessments(@PathVariable("studentId") Integer studentId, @PathVariable("courseId") Integer courseId) {
        return ResponseEntity.ok(assessmentService.getAllAssessmentByStudentAndCourse(studentId, courseId));
    }
}
