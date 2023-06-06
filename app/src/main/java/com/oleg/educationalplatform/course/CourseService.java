package com.oleg.educationalplatform.course;

import com.oleg.educationalplatform.course.studentcourse.StudentCourse;
import com.oleg.educationalplatform.course.studentcourse.StudentCourseRepository;
import com.oleg.educationalplatform.security.user.Role;
import com.oleg.educationalplatform.security.user.User;
import com.oleg.educationalplatform.security.user.UserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Data
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final StudentCourseRepository studentCourseRepository;
    private final UserRepository userRepository;

    public Course getCourseById(Integer id) {
        return courseRepository.findById(id)
                .orElseThrow();
    }

    public List<Course> getCourseByTeacherId(Integer teacherId) {
        return courseRepository.findAllByTeacherId(teacherId);
    }

    public List<Course> getCourseByTitle(String title) {
        return courseRepository.findAllByTitle(title);
    }

    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    public void deleteCourseById(Integer id) {
        courseRepository.deleteById(id);
    }

    public Course updateCourse(Course course) {
        return courseRepository.save(course);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public List<Course> getUsersAllCourses() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Course> courses = new ArrayList<>();
        if (user.getRole() == Role.STUDENT) {
            List<Integer> courseIds = studentCourseRepository.findCourseIdsByStudentId(user.getId());
            courses = courseRepository.findAllById(courseIds);
            return courses;
        } else if (user.getRole() == Role.TEACHER) {
            courses = courseRepository.findAllByTeacherId(user.getId());
        }
        return courses;
    }

    public List<Course> getUsersUnfinishedCourses() {
        return getUsersAllCourses().stream().filter((course) -> !course.isFinished()).collect(Collectors.toList());
    }

    public List<Course> getUsersFinishedCourses() {
        return getUsersAllCourses().stream().filter(Course::isFinished).collect(Collectors.toList());
    }

    public void addStudentOnCourse(StudentCourse studentCourse) {
        studentCourseRepository.save(studentCourse);
    }

    public List<User> getStudentsByCourse(Integer courseId) {
        return userRepository.findStudentsByCourseId(courseId);
    }

    public void deleteStudentOnCourse(Integer studentId, Integer courseId) {
        StudentCourse studentCourse = studentCourseRepository.findByStudentAndCourse(studentId, courseId);
        studentCourseRepository.delete(studentCourse);
    }

    public List<User> getAbsentStudentsByCourse(Integer courseId) {
        if (courseRepository.findById(courseId).isEmpty()) {
            return new ArrayList<>();
        };
        List<User> allStudents = userRepository.findAllStudents();
        List<User> courseStudents = userRepository.findStudentsByCourseId(courseId);
        allStudents.removeAll(courseStudents);
        return allStudents;
    }
}
