package com.oleg.educationalplatform.security.config;

import com.oleg.educationalplatform.assessment.Assessment;
import com.oleg.educationalplatform.assessment.AssessmentRepository;
import com.oleg.educationalplatform.course.Course;
import com.oleg.educationalplatform.course.CourseRepository;
import com.oleg.educationalplatform.course.studentcourse.StudentCourse;
import com.oleg.educationalplatform.course.studentcourse.StudentCourseRepository;
import com.oleg.educationalplatform.security.user.Role;
import com.oleg.educationalplatform.security.user.User;
import com.oleg.educationalplatform.security.user.UserRepository;
import com.oleg.educationalplatform.taskmodule.material.Material;
import com.oleg.educationalplatform.taskmodule.material.MaterialRespository;
import com.oleg.educationalplatform.taskmodule.task.Task;
import com.oleg.educationalplatform.taskmodule.task.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Profile("!test")
@Component
public class DataLoader implements ApplicationRunner {

    @Autowired
    private UserRepository repository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private StudentCourseRepository studentCourseRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private AssessmentRepository assessmentRepository;
    @Autowired
    private MaterialRespository materialRespository;

    @Override
    public void run(ApplicationArguments args) throws ParseException {
        User userAdmin = User.builder()
                .firstname("admin")
                .lastname("admin")
                .username("d")
                .password(passwordEncoder.encode("d"))
                .role(Role.ADMIN)
                .build();

        User userTeacher = User.builder()
                .firstname("teacher")
                .lastname("teacher")
                .username("q")
                .password(passwordEncoder.encode("q"))
                .role(Role.TEACHER)
                .build();

        User userTeacher1 = User.builder()
                .firstname("t1")
                .lastname("t1")
                .username("w")
                .password(passwordEncoder.encode("w"))
                .role(Role.TEACHER)
                .build();

        User userStudent = User.builder()
                .firstname("oleg")
                .lastname("mak")
                .username("a")
                .groupName("IP-12")
                .password(passwordEncoder.encode("f"))
                .role(Role.STUDENT)
                .build();

        User userStudent1 = User.builder()
                .firstname("babijon")
                .lastname("bibul")
                .username("b")
                .groupName("IP-12")
                .password(passwordEncoder.encode("b"))
                .role(Role.STUDENT)
                .build();

        User userStudent2 = User.builder()
                .firstname("jimmy")
                .lastname("yaya")
                .username("s")
                .groupName("IP-12")
                .password(passwordEncoder.encode("s"))
                .role(Role.STUDENT)
                .build();

        Course course = Course.builder()
                .beginDate(new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").parse("01/01/2023 00:00:00"))
                .endDate(new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").parse("07/01/2023 00:00:00"))
                .teacherId(2)
                .title("Math")
                .build();

        Course course2 = Course.builder()
                .beginDate(new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").parse("01/01/2022 00:00:00"))
                .endDate(new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").parse("06/01/2022 00:00:00"))
                .teacherId(2)
                .title("Physics")
                .build();

        Course course3 = Course.builder()
                .beginDate(new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").parse("12/01/2022 00:00:00"))
                .endDate(new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").parse("05/01/2023 00:00:00"))
                .teacherId(2)
                .title("English")
                .build();

        StudentCourse studentCourse = StudentCourse.builder()
                .courseId(1)
                .studentId(4)
                .build();

        StudentCourse studentCourse1 = StudentCourse.builder()
                .courseId(2)
                .studentId(4)
                .build();

        StudentCourse studentCourse2 = StudentCourse.builder()
                .courseId(3)
                .studentId(4)
                .build();

        StudentCourse studentCourse3 = StudentCourse.builder()
                .courseId(2)
                .studentId(5)
                .build();

        StudentCourse studentCourse4 = StudentCourse.builder()
                .courseId(3)
                .studentId(5)
                .build();

        Task task = Task.builder()
                .taskText("Do this")
                .title("Adding")
                .creationTime(new Date())
                .exposeTime(new Date(System.currentTimeMillis() + 3600))
                .courseId(1)
                .build();

        Task task1 = Task.builder()
                .taskText("abc")
                .title("Subtraction")
                .creationTime(new Date())
                .exposeTime(new Date(System.currentTimeMillis() + 3600000))
                .courseId(1)
                .build();

        Task task2 = Task.builder()
                .taskText("saldkfa;sdf")
                .title("Gravity")
                .creationTime(new Date())
                .exposeTime(new Date(System.currentTimeMillis() + 3600))
                .courseId(2)
                .build();

        Task task3 = Task.builder()
                .taskText("sdkgjapsfg")
                .title("ElectMagn")
                .creationTime(new Date())
                .exposeTime(new Date(System.currentTimeMillis() + 3600))
                .courseId(2)
                .build();

        Task task4 = Task.builder()
                .taskText("sdasdgs")
                .title("Speaking")
                .creationTime(new Date())
                .exposeTime(new Date(System.currentTimeMillis() + 3600))
                .courseId(3)
                .build();

        Assessment assessment = Assessment.builder()
                .date(new Date())
                .mark(7)
                .studentId(4)
                .taskId(52)
                .build();

        Assessment assessment1 = Assessment.builder()
                .date(new Date())
                .mark(10)
                .studentId(5)
                .taskId(56)
                .build();

        Assessment assessment2 = Assessment.builder()
                .date(new Date())
                .mark(5)
                .studentId(4)
                .taskId(55)
                .build();

        Assessment assessment3 = Assessment.builder()
                .date(new Date())
                .mark(6)
                .studentId(5)
                .taskId(55)
                .build();

        Material material = Material.builder()
                .courseId(1)
                .build();

        Material material2 = Material.builder()
                .courseId(2)
                .build();

        Material material3 = Material.builder()
                .courseId(3)
                .build();

        materialRespository.save(material);
        materialRespository.save(material2);
        materialRespository.save(material3);
        taskRepository.save(task);
        taskRepository.save(task1);
        taskRepository.save(task2);
        taskRepository.save(task3);
        taskRepository.save(task4);
        assessmentRepository.save(assessment);
        assessmentRepository.save(assessment1);
        assessmentRepository.save(assessment2);
        assessmentRepository.save(assessment3);
        courseRepository.save(course);
        courseRepository.save(course2);
        courseRepository.save(course3);
        studentCourseRepository.save(studentCourse);
        studentCourseRepository.save(studentCourse1);
        studentCourseRepository.save(studentCourse2);
        studentCourseRepository.save(studentCourse3);
        studentCourseRepository.save(studentCourse4);
        repository.save(userAdmin);
        repository.save(userTeacher);
        repository.save(userTeacher1);
        repository.save(userStudent);
        repository.save(userStudent1);
        repository.save(userStudent2);
    }
}
