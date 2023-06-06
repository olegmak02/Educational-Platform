package com.oleg.educationalplatform.course;

import com.oleg.educationalplatform.course.studentcourse.StudentCourse;
import com.oleg.educationalplatform.security.user.User;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Data
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/course")
public class CourseController {

    private final CourseService service;

    @GetMapping("/all")
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(service.getAllCourses());
    }

    @GetMapping("/my-courses")
    public ResponseEntity<List<Course>> getUsersCourses() {
        return ResponseEntity.ok(service.getUsersAllCourses());
    }

    @GetMapping("/my-courses/finished")
    public ResponseEntity<List<Course>> getStudentFinishedCourses() {
        return ResponseEntity.ok(service.getUsersFinishedCourses());
    }

    @GetMapping("/my-courses/unfinished")
    public ResponseEntity<List<Course>> getStudentUnfinishedCourses() {
        return ResponseEntity.ok(service.getUsersUnfinishedCourses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(service.getCourseById(id));
    }

    @GetMapping("/students/{courseId}")
    public ResponseEntity<List<User>> getStudentsByCourse(@PathVariable("courseId") Integer courseId) {
        return ResponseEntity.ok(service.getStudentsByCourse(courseId));
    }

    @GetMapping("/absent-students/{courseId}")
    public ResponseEntity<List<User>> getAbsentStudentsByCourse(@PathVariable("courseId") Integer courseId) {
        return ResponseEntity.ok(service.getAbsentStudentsByCourse(courseId));
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<Course>> getCoursesByTeacherId(@PathVariable("teacherId") Integer teacherId) {
        return ResponseEntity.ok(service.getCourseByTeacherId(teacherId));
    }

    @GetMapping("/title")
    public ResponseEntity<List<Course>> getCoursesByTitle(@RequestParam("title") String title) {
        return ResponseEntity.ok(service.getCourseByTitle(title));
    }

    @PostMapping("/create")
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {
        return ResponseEntity.ok(service.createCourse(course));
    }

    @PostMapping("/delete-student/course/{courseId}/student/{studentId}")
    public ResponseEntity<String> deleteStudent(@PathVariable("courseId") Integer courseId, @PathVariable("studentId") Integer studentId) {
        service.deleteStudentOnCourse(studentId, courseId);
        return ResponseEntity.ok("Student removed from course");
    }

    @PostMapping("/register-student")
    public ResponseEntity<String> addStudent(@RequestBody StudentCourse studentCourse) {
        service.addStudentOnCourse(studentCourse);
        return ResponseEntity.ok("Student registrated");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCourse(@PathVariable("id") Integer id) {
        service.deleteCourseById(id);
        return ResponseEntity.ok("Course deleted");
    }

    @PostMapping("/update")
    public ResponseEntity<Course> updateCourse(@RequestBody Course course) {
        return ResponseEntity.ok(service.updateCourse(course));
    }
}
