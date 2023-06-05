package com.oleg.educationalplatform.course.studentcourse;

import com.oleg.educationalplatform.assessment.Assessment;
import com.oleg.educationalplatform.assessment.AssessmentRepository;
import com.oleg.educationalplatform.course.Course;
import com.oleg.educationalplatform.course.CourseRepository;
import com.oleg.educationalplatform.security.user.User;
import com.oleg.educationalplatform.security.user.UserRepository;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Configuration
@EnableScheduling
public class Scheduler {

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AssessmentRepository assessmentRepository;
    @Autowired
    private StudentCourseRepository studentCourseRepository;

    @Scheduled(cron = "1 0 0 * * ?")
    public void scheduledAssessment() {
        Date now = Timestamp.from(Instant.now());

        for (Course course: courseRepository.findAll()) {
            if (DateUtils.isSameDay(now, course.getEndDate())) {
                List<User> students = userRepository.findStudentsByCourseId(course.getId());
                for (User student: students) {
                    Integer totalMark = assessmentRepository.findAllByCourseId(student.getId(), course.getId()).stream().map(Assessment::getMark).reduce(0, Integer::sum);
                    StudentCourse studentCourse = studentCourseRepository.findByStudentAndCourse(student.getId(), course.getId());
                    studentCourse.setMark(totalMark);
                    studentCourseRepository.save(studentCourse);
                }
            }
        }
    }
}
