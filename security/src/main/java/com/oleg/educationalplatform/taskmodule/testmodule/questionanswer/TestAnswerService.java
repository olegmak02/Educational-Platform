package com.oleg.educationalplatform.taskmodule.testmodule.questionanswer;

import com.oleg.educationalplatform.assessment.Assessment;
import com.oleg.educationalplatform.assessment.AssessmentRepository;
import com.oleg.educationalplatform.security.user.User;
import com.oleg.educationalplatform.taskmodule.testmodule.questiontest.TestQuestion;
import com.oleg.educationalplatform.taskmodule.testmodule.questiontest.TestQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TestAnswerService {
    private final TestQuestionRepository questionRepository;
    private final TestAnswerRepository testAnswerRepository;
    private final AssessmentRepository assessmentRepository;

    public void submitAnswer(List<QuestionAnswer> answers) {
        User currentStudent = (User) (SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        double mark = 0;
        for (QuestionAnswer currentAnswer : answers) {

            TestQuestion cor = questionRepository.findById(currentAnswer.getQuestionId()).orElseThrow();
            mark += cor.getMark() * checkMultianswer(currentAnswer.getAnswer(), cor.getCorrect(), cor.getOptions().size());
        }
        mark = Math.round(mark);

        TestAnswer answer = testAnswerRepository.findByTestId(testAnswerRepository.findById(answers.get(0).getTestAnswerId()).get().getTestId(), currentStudent.getId());
        answer.setMark(mark);

        testAnswerRepository.save(answer);

        Assessment assessment = Assessment.builder()
                .taskId(answer.getTestId())
                .studentId(currentStudent.getId())
                .mark((int) mark)
                .date(new Date())
                .build();

        assessmentRepository.save(assessment);
    }


    private double checkMultianswer(String answer, List<String> correctAnswer, int optionsNumber) {
        List<String> answers = new ArrayList<>(Arrays.asList(answer.split("~")));
        List<String> answerCopy = new ArrayList<>(answers);
        answers.retainAll(correctAnswer);
        answerCopy.removeAll(correctAnswer);
        return (answers.size() + (optionsNumber - correctAnswer.size() - answerCopy.size())) / (double)optionsNumber;
    }

    public TestAnswer saveTestAnswer(TestAnswer testAnswer) {
        User currentUser = (User) (SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        testAnswer.setStudentId(currentUser.getId());
        testAnswer.setDate(new Date());
        return testAnswerRepository.save(testAnswer);
    }

    public TestAnswer getAnswerByTestId(Integer testId) {
        User currentUser = (User) (SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return testAnswerRepository.findByTestId(testId, currentUser.getId());
    }

    public List<TestAnswer> getTestAnswersByCourse(Integer courseId) {
        User currentUser = (User) (SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return testAnswerRepository.findAnswersByCourse(currentUser.getId(), courseId);
    }
}