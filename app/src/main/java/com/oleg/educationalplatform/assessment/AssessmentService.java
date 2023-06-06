package com.oleg.educationalplatform.assessment;

import com.oleg.educationalplatform.security.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssessmentService {
    private final AssessmentRepository assessmentRepository;

    public List<Assessment> getAssessmentsByCourseId(Integer courseId) {
        User student = (User) (SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return assessmentRepository.findAllByCourseId(student.getId(), courseId);
    }

    public Assessment createAssessment(Assessment assessment) {
        if (assessment.getDate() != null) {
            assessment.setDate(new Date());
        }
        return assessmentRepository.save(assessment);
    }

    public List<Assessment> getAllAssessmentByStudentAndCourse(Integer studentId, Integer courseId) {
        return assessmentRepository.findAllByCourseId(studentId, courseId);
    }

    public Assessment updateMarkAssessment(Assessment assessment) {
        Assessment foundAssessment = assessmentRepository.findById(assessment.getId()).orElseThrow();
        foundAssessment.setMark(assessment.getMark());
        foundAssessment.setDate(new Date());
        return assessmentRepository.save(foundAssessment);
    }
}
