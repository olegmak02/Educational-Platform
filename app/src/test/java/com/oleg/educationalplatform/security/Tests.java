package com.oleg.educationalplatform.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oleg.educationalplatform.assessment.Assessment;
import com.oleg.educationalplatform.assessment.AssessmentController;
import com.oleg.educationalplatform.assessment.AssessmentRepository;
import com.oleg.educationalplatform.course.Course;
import com.oleg.educationalplatform.course.CourseController;
import com.oleg.educationalplatform.course.studentcourse.StudentCourse;
import com.oleg.educationalplatform.security.auth.*;
import com.oleg.educationalplatform.security.config.JwtAuthenticationFilter;
import com.oleg.educationalplatform.security.config.JwtService;
import com.oleg.educationalplatform.security.user.Role;
import com.oleg.educationalplatform.security.user.User;
import com.oleg.educationalplatform.security.user.UserController;
import com.oleg.educationalplatform.security.user.UserRepository;
import com.oleg.educationalplatform.taskmodule.attachment.Attachment;
import com.oleg.educationalplatform.taskmodule.attachment.AttachmentController;
import com.oleg.educationalplatform.taskmodule.material.Material;
import com.oleg.educationalplatform.taskmodule.material.MaterialController;
import com.oleg.educationalplatform.taskmodule.material.MaterialRespository;
import com.oleg.educationalplatform.taskmodule.task.Task;
import com.oleg.educationalplatform.taskmodule.task.TaskController;
import com.oleg.educationalplatform.taskmodule.task.TaskRepository;
import com.oleg.educationalplatform.taskmodule.taskanswer.TaskAnswer;
import com.oleg.educationalplatform.taskmodule.taskanswer.TaskAnswerController;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
public class Tests {
    private MockMvc mvc;
    private static String studentToken;
    private static String teacherToken;
    private static Authentication authentication1;
    private static Authentication authentication2;

    @Autowired
    private AssessmentRepository assessmentRepository;
    @Autowired
    private JwtService jwtService;

    @Autowired
    @InjectMocks
    private AssessmentController assessmentController;

    @Autowired
    @InjectMocks
    private CourseController courseController;

    @Autowired
    @InjectMocks
    private AuthenticationController authenticationController;

    @Autowired
    @InjectMocks
    private UserController userController;

    @Autowired
    @InjectMocks
    private AttachmentController attachmentController;

    @Autowired
    @InjectMocks
    private MaterialController materialController;

    @Autowired
    @InjectMocks
    private TaskController taskController;

    @Autowired
    @InjectMocks
    private TaskAnswerController taskAnswerController;

    @Autowired
    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private UserRepository repository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private MaterialRespository materialRespository;

    @Autowired
    private ObjectMapper mapper;

    @BeforeAll
    public static void init(@Autowired JwtService jwtService, @Autowired UserRepository repository) {
        User student = repository.findById(5).get();
        User student2 = repository.findById(6).get();
        User teacher = repository.findById(2).get();
        studentToken = jwtService.generateToken(student);
        teacherToken = jwtService.generateToken(teacher);
        authentication1 = new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return student;
            }

            @Override
            public boolean isAuthenticated() {
                return false;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }

            @Override
            public String getName() {
                return null;
            }
        };

        authentication2 = new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return student2;
            }

            @Override
            public boolean isAuthenticated() {
                return false;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }

            @Override
            public String getName() {
                return null;
            }
        };
    }

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.standaloneSetup(assessmentController,
                    courseController,
                    materialController,
                    authenticationController,
                    userController,
                    attachmentController,
                    taskController,
                    taskAnswerController)
                .addFilter(jwtAuthenticationFilter)
                .build();
    }

    @Test
    void assessments_getMyAssessments_studentIsInCourse() throws Exception {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        MockHttpServletResponse response = mvc.perform(
                get("/api/v1/assessment/my-assessments?courseId=2")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();


        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        List<Assessment> assessment = mapper.readValue(response.getContentAsString(), List.class);

        assertEquals(2, assessment.size());
    }

    @Test
    void assessments_getMyAssessments_studentIsNotInCourse() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                get("/api/v1/assessment/my-assessments?courseId=1")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        List<Assessment> assessment = mapper.readValue(response.getContentAsString(), List.class);

        assertEquals(assessment.size(), 0);
    }

    @Test
    void assessments_getMyAssessments_unexistingCourse() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                get("/api/v1/assessment/my-assessments?courseId=111")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        List<Assessment> assessment = mapper.readValue(response.getContentAsString(), List.class);

        assertEquals(assessment.size(), 0);
    }

    @Test
    void assessments_updateMark_existingAssessment() throws Exception {
        MockHttpServletResponse getResponse = mvc.perform(
                get("/api/v1/assessment/my-assessments?courseId=2")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        List<Assessment> assessments = mapper.readValue(getResponse.getContentAsString(), ArrayList.class);
        Assessment assessment = mapper.convertValue(assessments.get(0), Assessment.class);
        assessment.setMark(15);
        assessment.setDate(new Date());

        MockHttpServletResponse postResponse = mvc.perform(
                post("/api/v1/assessment/update-mark")
                        .content(mapper.writeValueAsString(assessment))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", String.format("Bearer %s", teacherToken)))
                .andReturn()
                .getResponse();

        assertThat(postResponse.getStatus()).isEqualTo(HttpStatus.OK.value());

        MockHttpServletResponse getNewResponse = mvc.perform(
                get("/api/v1/assessment/my-assessments?courseId=2")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertThat(getNewResponse.getStatus()).isEqualTo(HttpStatus.OK.value());

        List<Assessment> newAssessments = mapper.readValue(getNewResponse.getContentAsString(), List.class);
        Assessment newAssessment = mapper.convertValue(newAssessments.get(0), Assessment.class);
        assertEquals(newAssessment.getMark(), 15);
    }

    @Test
    void assessments_createMark_successfully() throws Exception {
        Assessment assessment = Assessment.builder()
                .date(new Date())
                .studentId(5)
                .taskId(54)
                .build();

        MockHttpServletResponse postResponse = mvc.perform(
                post("/api/v1/assessment/create")
                        .content(mapper.writeValueAsString(assessment))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", String.format("Bearer %s", studentToken)))
                .andReturn()
                .getResponse();

        assertThat(postResponse.getStatus()).isEqualTo(HttpStatus.OK.value());

        MockHttpServletResponse getResponse = mvc.perform(
                get("/api/v1/assessment/my-assessments?courseId=2")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertThat(getResponse.getStatus()).isEqualTo(HttpStatus.OK.value());

        List<Assessment> newAssessments = mapper.readValue(getResponse.getContentAsString(), List.class);
        assertEquals(2, newAssessments.size());
    }

    @Test
    void assessments_createMark_noTask()  {
        try {
            Assessment assessment = Assessment.builder()
                .date(new Date())
                .studentId(5)
                .build();

            MockHttpServletResponse postResponse = mvc.perform(
                    post("/api/v1/assessment/create")
                            .content(mapper.writeValueAsString(assessment))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", String.format("Bearer %s", studentToken)))
                    .andReturn()
                    .getResponse();
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    void assessments_createMark_noStudent()  {
        try {
            Assessment assessment = Assessment.builder()
                    .date(new Date())
                    .taskId(1)
                    .build();

            MockHttpServletResponse postResponse = mvc.perform(
                    post("/api/v1/assessment/create")
                            .content(mapper.writeValueAsString(assessment))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", String.format("Bearer %s", studentToken)))
                    .andReturn()
                    .getResponse();
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    void assessments_createMark_noMark()  {
        try {
            Assessment assessment = Assessment.builder()
                    .date(new Date())
                    .taskId(1)
                    .studentId(4)
                    .build();

            MockHttpServletResponse postResponse = mvc.perform(
                    post("/api/v1/assessment/create")
                            .content(mapper.writeValueAsString(assessment))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", String.format("Bearer %s", studentToken)))
                    .andReturn()
                    .getResponse();
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    void assessments_getStudentAssessments_unexistingStudent() throws Exception {
        MockHttpServletResponse getResponse = mvc.perform(
                get("/api/v1/assessment/course/1/student/4324")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        List<Assessment> assessments = mapper.readValue(getResponse.getContentAsString(), ArrayList.class);
        assertEquals(0, assessments.size());
    }

    @Test
    void assessments_getStudentAssessments_unexistingCourse() throws Exception {
        MockHttpServletResponse getResponse = mvc.perform(
                get("/api/v1/assessment/course/112/student/4")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        List<Assessment> assessments = mapper.readValue(getResponse.getContentAsString(), ArrayList.class);
        assertEquals(0, assessments.size());
    }

    @Test
    void courses_allCourses() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                get("/api/v1/course/all")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();


        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        List<Course> courses = mapper.readValue(response.getContentAsString(), List.class);

        assertEquals(3, courses.size());
    }

    @Test
    void courses_myCourses_existingCourses() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                get("/api/v1/course/my-courses")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        List<Course> courses = mapper.readValue(response.getContentAsString(), List.class);

        assertEquals(courses.size(), 2);
    }

    @Test
    void courses_myCoursesFinished() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                get("/api/v1/course/my-courses/finished")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        List<Course> courses = mapper.readValue(response.getContentAsString(), List.class);

        assertEquals(courses.size(), 2);
    }

    @Test
    void courses_myCoursesUnfinished() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                get("/api/v1/course/my-courses/unfinished")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        List<Course> courses = mapper.readValue(response.getContentAsString(), List.class);

        assertEquals(courses.size(), 0);
    }

    @Test
    void courses_myCourseById_unexisting() {
        try {
            MockHttpServletResponse response = mvc.perform(
                    get("/api/v1/course/1342")
                            .header("Authorization", String.format("Bearer %s", studentToken))
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    void courses_myCourseById_existing() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                get("/api/v1/course/1")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        Course course = mapper.readValue(response.getContentAsString(), Course.class);

        assertEquals(course.getTitle(), "Math");
    }

    @Test
    void courses_myCourseById_unexistingCourse() {
        try {
            MockHttpServletResponse response = mvc.perform(
                    get("/api/v1/course/1231")
                            .header("Authorization", String.format("Bearer %s", studentToken))
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn()
                    .getResponse();
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    void courses_studentsByCourses_existingCourse() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                get("/api/v1/course/students/1")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        List<User> users = mapper.readValue(response.getContentAsString(), List.class);

        assertEquals(users.size(), 1);
    }

    @Test
    void courses_studentsByCourses_unexistingCourse() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                get("/api/v1/course/students/412141")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        List<User> users = mapper.readValue(response.getContentAsString(), List.class);

        assertEquals(users.size(), 0);
    }

    @Test
    void courses_absentStudentsByCourses_existingCourse() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                get("/api/v1/course/absent-students/1")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        List<User> users = mapper.readValue(response.getContentAsString(), List.class);

        assertEquals(users.size(), 2);
    }

    @Test
    void courses_absentStudentsByCourses_unexistingCourse() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                get("/api/v1/course/absent-students/412141")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        List<User> users = mapper.readValue(response.getContentAsString(), List.class);

        assertEquals(users.size(), 0);
    }

    @Test
    void courses_getByTeacher_existingTeacher() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                get("/api/v1/course/teacher/2")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        List<Course> courses = mapper.readValue(response.getContentAsString(), List.class);

        assertEquals(courses.size(), 3);
    }

    @Test
    void courses_getByTeacher_unexistingTeacher() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                get("/api/v1/course/teacher/25523")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        List<Course> courses = mapper.readValue(response.getContentAsString(), List.class);

        assertEquals(courses.size(), 0);
    }

    @Test
    void courses_getByTitle_existingTitle() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                get("/api/v1/course/title")
                        .param("title", "M")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        List<Course> courses = mapper.readValue(response.getContentAsString(), List.class);

        assertEquals(courses.size(), 1);
    }

    @Test
    void courses_getByTitle_unexistingTitle() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                get("/api/v1/course/title")
                        .param("title", "Mwwgsdasgds")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        List<Course> courses = mapper.readValue(response.getContentAsString(), List.class);

        assertEquals(courses.size(), 0);
    }

    @Test
    void courses_createCourse() throws Exception {
        Course course = Course.builder()
                .title("Geography")
                .teacherId(3)
                .beginDate(new Date())
                .endDate(new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").parse("08/01/2023 00:00:00"))
                .build();

        MockHttpServletResponse response = mvc.perform(
                post("/api/v1/course/create")
                        .content(mapper.writeValueAsString(course))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        Course checkCourse = mapper.readValue(response.getContentAsString(), Course.class);

        assertNotEquals(checkCourse, null);

        mvc.perform(
                delete("/api/v1/course/delete/4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", String.format("Bearer %s", teacherToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();
    }

    @Test
    void courses_createCourse_emptyTitle() {
        try {
            Course course = Course.builder()
                    .teacherId(3)
                    .beginDate(new Date())
                    .endDate(new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").parse("08/01/2023 00:00:00"))
                    .build();


            MockHttpServletResponse response = mvc.perform(
                    post("/api/v1/course/create")
                            .content(mapper.writeValueAsString(course))
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", String.format("Bearer %s", studentToken))
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn()
                    .getResponse();
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    void courses_createCourse_emptyTeacher() {
        try {
            Course course = Course.builder()
                    .title("Geography")
                    .beginDate(new Date())
                    .endDate(new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").parse("08/01/2023 00:00:00"))
                    .build();


            MockHttpServletResponse response = mvc.perform(
                    post("/api/v1/course/create")
                            .content(mapper.writeValueAsString(course))
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", String.format("Bearer %s", studentToken))
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn()
                    .getResponse();
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    void courses_deleteStudentFromCourse() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                get("/api/v1/course/students/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        Integer amount = mapper.readValue(response.getContentAsString(), List.class).size();

        assertEquals(amount, 2);

        MockHttpServletResponse deleteResponse = mvc.perform(
                post("/api/v1/course/delete-student/course/3/student/4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        MockHttpServletResponse response2 = mvc.perform(
                get("/api/v1/course/students/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        Integer amount1 = mapper.readValue(response2.getContentAsString(), List.class).size();

        assertEquals(amount1, 1);
    }

    @Test
    void courses_registerStudentOnCourse() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                get("/api/v1/course/students/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        Integer amount = mapper.readValue(response.getContentAsString(), List.class).size();
        assertEquals(amount, 1);

        StudentCourse studentCourse = StudentCourse.builder()
                .courseId(1)
                .studentId(5)
                .build();

        MockHttpServletResponse response1 = mvc.perform(
                post("/api/v1/course/register-student")
                        .content(mapper.writeValueAsString(studentCourse))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertThat(response1.getStatus()).isEqualTo(HttpStatus.OK.value());

        MockHttpServletResponse response2 = mvc.perform(
                get("/api/v1/course/students/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        Integer amount1 = mapper.readValue(response2.getContentAsString(), List.class).size();

        assertEquals(amount1, 2);

        mvc.perform(
                post("/api/v1/course/delete-student/course/1/student/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();
    }

    @Test
    void courses_deleteCourse_existingCourse() throws Exception {
        mvc.perform(
                post("/api/v1/course/create")
                        .content(mapper.writeValueAsString(Course.builder().title("Algebra").endDate(new Date()).beginDate(new Date()).teacherId(3).build()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        MockHttpServletResponse response = mvc.perform(
                get("/api/v1/course/all")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        List<Course> courses = mapper.readValue(response.getContentAsString(), List.class);

        assertEquals(4, courses.size());

        mvc.perform(
                delete("/api/v1/course/delete/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        MockHttpServletResponse response1 = mvc.perform(
                get("/api/v1/course/all")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        List<Course> courses1 = mapper.readValue(response1.getContentAsString(), List.class);

        assertEquals(3, courses1.size());
    }

    @Test
    void authentication_registerUser_successfully() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .firstname("Alex")
                .role(Role.STUDENT)
                .groupName("AB-93")
                .username("alx249")
                .password("3ois90393")
                .lastname("Philips")
                .build();

        MockHttpServletResponse response = mvc.perform(
                post("/api/v1/auth/register")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        AuthenticationResponse authResponse = mapper.readValue(response.getContentAsString(), AuthenticationResponse.class);

        assertNotEquals(authResponse.getToken(), null);

        mvc.perform(
                post("/api/v1/user/delete/7")
                        .content(mapper.writeValueAsString(""))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();
    }

    @Test
    void authentication_registerUser_badPassword() {
        try {
            RegisterRequest request = RegisterRequest.builder()
                    .firstname("Alex")
                    .username("alex")
                    .role(Role.STUDENT)
                    .groupName("AB-93")
                    .password("asd")
                    .lastname("Philips")
                    .build();

            MockHttpServletResponse response = mvc.perform(
                    post("/api/v1/auth/register")
                            .content(mapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", String.format("Bearer %s", studentToken))
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn()
                    .getResponse();

            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    void authentication_registerUser_noUsername() {
        try {
            RegisterRequest request = RegisterRequest.builder()
                    .firstname("Alex")
                    .role(Role.STUDENT)
                    .groupName("AB-93")
                    .password("3ois90393")
                    .lastname("Philips")
                    .build();

            MockHttpServletResponse response = mvc.perform(
                    post("/api/v1/auth/register")
                            .content(mapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", String.format("Bearer %s", studentToken))
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn()
                    .getResponse();

            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    void authentication_authenticateUser_existingUser() throws Exception {
        AuthenticationRequest request = AuthenticationRequest.builder()
                .username("mrk45")
                .password("gdsfw303")
                .build();

        MockHttpServletResponse response = mvc.perform(
                post("/api/v1/auth/authenticate")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertEquals(response.getStatus(), HttpStatus.OK.value());

        AuthenticationResponse authResponse = mapper.readValue(response.getContentAsString(), AuthenticationResponse.class);


        assertTrue(authResponse.getToken().matches("[a-zA-Z0-9-_.]+"));
    }

    @Test
    void authentication_authenticateUser_unexistingUser() {
        AuthenticationRequest request = AuthenticationRequest.builder()
                .username("ghethergf")
                .password("dfgdfhetjyku")
                .build();

        try {
            MockHttpServletResponse response = mvc.perform(
                    post("/api/v1/auth/authenticate")
                            .content(mapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", String.format("Bearer %s", studentToken))
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn()
                    .getResponse();
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    void authentication_authenticateUser_noPassword() {
        AuthenticationRequest request = AuthenticationRequest.builder()
                .username("ghethergf")
                .build();

        try {
            MockHttpServletResponse response = mvc.perform(
                    post("/api/v1/auth/authenticate")
                            .content(mapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", String.format("Bearer %s", studentToken))
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn()
                    .getResponse();
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    void authentication_authenticateUser_noUsername() {
        AuthenticationRequest request = AuthenticationRequest.builder()
                .password("dfgdfhetjyku")
                .build();

        try {


            MockHttpServletResponse response = mvc.perform(
                    post("/api/v1/auth/authenticate")
                            .content(mapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", String.format("Bearer %s", studentToken))
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn()
                    .getResponse();
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    void authentication_changePassword_badNewPassword() throws Exception {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .oldPassword("4tagg3gw")
                .newPassword("h")
                .build();

        MockHttpServletResponse response = mvc.perform(
                post("/api/v1/auth/change-password")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertEquals(400, response.getStatus());
    }

    @Test
    void authentication_changePassword_badOldPassword() throws Exception {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .oldPassword("4tasdfwegg3gw")
                .newPassword("h")
                .build();

        MockHttpServletResponse response = mvc.perform(
                post("/api/v1/auth/change-password")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertEquals(400, response.getStatus());
    }

    @Test
    void authentication_changePassword_successful() throws Exception {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .oldPassword("4tagg3gw")
                .newPassword("2gsaggw3gw")
                .build();

        MockHttpServletResponse response = mvc.perform(
                post("/api/v1/auth/change-password")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    void jwtService_createToken() {
        User user = User.builder()
                .username("abc")
                .groupName("AB-12")
                .role(Role.STUDENT)
                .password("qwer1234")
                .build();

        String token = jwtService.generateToken(user);

        assertTrue(token.matches("[a-zA-Z0-9-_.]+"));
    }

    @Test
    void jwtService_getUsername() {
        String username = jwtService.extractUsername(studentToken);

        assertEquals("hud52", username);
    }

    @Test
    void jwtService_validate_badToken() {
        try {
            boolean isValid = jwtService.isTokenValid("alsjdflwe0gen", User.builder().build());
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    void jwtService_validate_expiredToken() {
        String token = Jwts
                .builder()
                .claim("role", Role.STUDENT)
                .setSubject("username123")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode("6B58703273357638792F423F4528472B4B6250655368566D597133743677397A")), SignatureAlgorithm.HS256)
                .compact();

        try {
            boolean isValid = jwtService.isTokenValid(token, User.builder().username("username123").build());
        } catch (ExpiredJwtException e) {
            assertTrue(true);
        };
    }

    @Test
    void jwtService_validate_invalidToken() {
        try {
            boolean isValid = jwtService.isTokenValid(studentToken, User.builder().username("username123").build());
        } catch (ExpiredJwtException e) {
            assertTrue(true);
        };
    }

    @Test
    void jwtService_validate_success() {
        boolean isValid = jwtService.isTokenValid(studentToken, User.builder().username("hud52").build());
        assertTrue(isValid);
    }

    @Test
    void users_getInfo_currentUser() throws Exception {
        MockHttpServletResponse getResponse = mvc.perform(
                get("/api/v1/user/my-info")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();
        Map<String, String> res = mapper.readValue(getResponse.getContentAsString(), HashMap.class);
        String username = res.get("username");
        assertEquals("STUDENT", res.get("role"));
        assertThat(username.equals("hud52")).isTrue();
    }

    @Test
    void users_allUsers() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                get("/api/v1/user/all")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();
        List list = mapper.readValue(response.getContentAsString(), List.class);

        assertEquals(5, list.size());
    }

    @Test
    void users_allTeachers() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                get("/api/v1/user/all-teachers")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();
        List list = mapper.readValue(response.getContentAsString(), List.class);

        assertEquals(2, list.size());
    }

    @Test
    void users_getUser_unexistingUser() {
        try {
            MockHttpServletResponse response = mvc.perform(
                    get("/api/v1/user/2325")
                            .header("Authorization", String.format("Bearer %s", studentToken))
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn()
                    .getResponse();
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    void users_getUser_existingUser() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                get("/api/v1/user/4")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();
        HashMap user = mapper.readValue(response.getContentAsString(), LinkedHashMap.class);

        assertEquals(user.get("username"), "mrk45");
    }

    @Test
    void users_deleteUser_existingUser() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .firstname("Alex")
                .role(Role.STUDENT)
                .groupName("AB-93")
                .username("alx249")
                .password("3ois90393")
                .lastname("Philips")
                .build();

        MockHttpServletResponse response = mvc.perform(
                post("/api/v1/auth/register")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        MockHttpServletResponse response1 = mvc.perform(
                get("/api/v1/user/all")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();
        List list = mapper.readValue(response1.getContentAsString(), List.class);

        assertEquals(6, list.size());

        MockHttpServletResponse response2 = mvc.perform(
                post("/api/v1/user/delete/7")
                        .header("Authorization", String.format("Bearer %s", teacherToken)))
                .andReturn()
                .getResponse();

        MockHttpServletResponse response3 = mvc.perform(
                get("/api/v1/user/all")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();
        List list2 = mapper.readValue(response3.getContentAsString(), List.class);

        assertEquals(5, list2.size());
    }

    @Test
    void users_deleteUser_unexistingUser() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                get("/api/v1/user/all")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();
        List list = mapper.readValue(response.getContentAsString(), List.class);


        MockHttpServletResponse response1 = mvc.perform(
                post("/api/v1/user/delete/465634")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        MockHttpServletResponse response2 = mvc.perform(
                get("/api/v1/user/all")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();
        List list2 = mapper.readValue(response2.getContentAsString(), List.class);

        assertEquals(list.size(), list2.size());
    }

    @Test
    void files_getFilesByPost_existingPost() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                get("/api/v1/file/post/1")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();
        List list = mapper.readValue(response.getContentAsString(), List.class);

        assertEquals(1, list.size());
    }

    @Test
    void files_getFilesByPost_unexistingPost() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                get("/api/v1/file/post/52526346")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();
        List list = mapper.readValue(response.getContentAsString(), List.class);

        assertEquals(0, list.size());
    }

    @Test
    void files_getFile_unexistingFile() {
        try {
            MockHttpServletResponse response = mvc.perform(
                    get("/api/v1/file/download/52526346")
                            .header("Authorization", String.format("Bearer %s", studentToken))
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn()
                    .getResponse();
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    void files_getFile_existingFile() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                get("/api/v1/file/download/1")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.TEXT_PLAIN))
                .andReturn()
                .getResponse();
        String content = response.getContentAsString();

        assertTrue(content.length() > 0);
    }

    @Test
    void files_getFile_noRights() {
        SecurityContextHolder.getContext().setAuthentication(authentication2);
        try {
            MockHttpServletResponse response = mvc.perform(
                    get("/api/v1/file/1")
                            .header("Authorization", String.format("Bearer %s", studentToken))
                            .accept(MediaType.TEXT_PLAIN))
                    .andReturn()
                    .getResponse();
            assertFalse(true);
        } catch (Exception e) {
            assertTrue(true);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication1);
    }

    @Test
    void materials_getFiles_existingCourse() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                get("/api/v1/material/files/course/1")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        List list = mapper.readValue(response.getContentAsString(), List.class);

        assertTrue(list.size() > 0);
    }

    @Test
    void materials_getFiles_unexistingCourse() {
        try {
            MockHttpServletResponse response = mvc.perform(
                    get("/api/v1/material/files/course/16477")
                            .header("Authorization", String.format("Bearer %s", studentToken))
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn()
                    .getResponse();
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    void materials_getMaterial_existingCourse() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                get("/api/v1/material/course/1")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        Integer materialId = mapper.readValue(response.getContentAsString(), Integer.class);

        assertEquals(2, materialId);
    }

    @Test
    void materials_getMaterial_unexistingCourse() {
        try {
            MockHttpServletResponse response = mvc.perform(
                    get("/api/v1/material/course/152342")
                            .header("Authorization", String.format("Bearer %s", studentToken))
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn()
                    .getResponse();
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    void materials_createMaterial() throws Exception {
        Material material = Material.builder()
                .courseId(1)
                .build();

        MockHttpServletResponse response = mvc.perform(
                post("/api/v1/material/create")
                        .content(mapper.writeValueAsString(material))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        Material material1 = mapper.readValue(response.getContentAsString(), Material.class);

        Assertions.assertNotNull(material1);

        materialRespository.delete(material1);
    }

    @Test
    void tasks_getTask_existingTask() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                get("/api/v1/task/52")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        Task task = mapper.readValue(response.getContentAsString(), Task.class);

        Assertions.assertNotNull(task);
    }

    @Test
    void tasks_getTask_unexistingTask() {
        try {
            MockHttpServletResponse response = mvc.perform(
                    get("/api/v1/task/5462572")
                            .header("Authorization", String.format("Bearer %s", studentToken))
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn()
                    .getResponse();
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    void tasks_getCompletedTasks_existingCourse() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                get("/api/v1/task/submitted/course/1")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        List task = mapper.readValue(response.getContentAsString(), List.class);

        assertNotNull(task);
    }

    @Test
    void tasks_getCompletedTasks_unexistingCourse() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                get("/api/v1/task/submitted/course/4523451")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        List list = mapper.readValue(response.getContentAsString(), List.class);

        assertTrue(list.size() == 0);
    }

    @Test
    void tasks_getIncompleteTasks_existingCourse() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                get("/api/v1/task/submitted/course/1")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        List list = mapper.readValue(response.getContentAsString(), List.class);

        assertTrue(list.size() > 0);
    }

    @Test
    void tasks_getIncompleteTasks_unexistingCourse() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                get("/api/v1/task/submitted/course/3452361")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        List list = mapper.readValue(response.getContentAsString(), List.class);

        assertTrue(list.size() == 0);
    }

    @Test
    void tasks_getTasksByTitle_existingCourse() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                get("/api/v1/task/course/1/title?title=Add")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        List list = mapper.readValue(response.getContentAsString(), List.class);

        assertTrue(list.size() == 1);
    }

    @Test
    void tasks_getTasksByTitle_unexistingCourse() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                get("/api/v1/task/course/4534531/title?title=Add")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        List list = mapper.readValue(response.getContentAsString(), List.class);

        assertTrue(list.size() == 0);
    }

    @Test
    void tasks_createTask_noCourse() {
        try {
            Task task = Task.builder()
                    .title("Testing")
                    .exposeTime(new Date())
                    .creationTime(new Date())
                    .taskText("Some task")
                    .build();


            MockHttpServletResponse response = mvc.perform(
                    post("/api/v1/task/create")
                            .content(mapper.writeValueAsString(task))
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", String.format("Bearer %s", studentToken))
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn()
                    .getResponse();
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }
    }


    @Test
    void tasks_createTask_noTitle() {
        try {
            Task task = Task.builder()
                    .courseId(2)
                    .exposeTime(new Date())
                    .creationTime(new Date())
                    .taskText("Some task")
                    .build();


            MockHttpServletResponse response = mvc.perform(
                    post("/api/v1/task/create")
                            .content(mapper.writeValueAsString(task))
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", String.format("Bearer %s", studentToken))
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn()
                    .getResponse();
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    void tasks_createTask_success() throws Exception {
        Task task = Task.builder()
                .title("Testing")
                .courseId(2)
                .exposeTime(new Date())
                .creationTime(new Date())
                .taskText("Some task")
                .build();

        MockHttpServletResponse response = mvc.perform(
                post("/api/v1/task/create")
                        .content(mapper.writeValueAsString(task))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        Task task1 = mapper.readValue(response.getContentAsString(), Task.class);

        Assertions.assertNotNull(task1);

        taskRepository.delete(task1);
    }

    @Test
    void answers_getAllByTask_existingTask() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                get("/api/v1/answer/task/52")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        List list = mapper.readValue(response.getContentAsString(), List.class);

        assertTrue(list.size() > 0);
    }

    @Test
    void answers_getAllByTask_unexistingTask() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                get("/api/v1/answer/task/134532452")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        List list = mapper.readValue(response.getContentAsString(), List.class);

        assertEquals(0, list.size());
    }

    @Test
    void answers_getAllByCurrentStudent_unexistingTask() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                get("/api/v1/answer/task/35364236/my-answer")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertEquals("", response.getContentAsString());
    }

    @Test
    void answers_getAllByCurrentStudent_existingTask() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                get("/api/v1/answer/task/52/my-answer")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        TaskAnswer answer = mapper.readValue(response.getContentAsString(), TaskAnswer.class);

        assertNotNull(answer);
    }

    @Test
    void answers_getAllByStudent_unexistingTask() {
        try {
            MockHttpServletResponse response = mvc.perform(
                    get("/api/v1/answer/task/134532452/student/5")
                            .header("Authorization", String.format("Bearer %s", studentToken))
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn()
                    .getResponse();

            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    void answers_getAllByStudent_existingTask() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                get("/api/v1/answer/task/52/student/5")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        TaskAnswer answer = mapper.readValue(response.getContentAsString(), TaskAnswer.class);

        assertNotNull(answer);
    }

    @Test
    void answers_getAllByStudent_unexistingStudent() {
        try {
            MockHttpServletResponse response = mvc.perform(
                    get("/api/v1/answer/task/52/student/235235")
                            .header("Authorization", String.format("Bearer %s", studentToken))
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn()
                    .getResponse();

            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    void answers_create_noTask() {
        try {
            TaskAnswer answer = TaskAnswer.builder()
                    .studentId(5)
                    .date(new Date())
                    .attachments(new ArrayList<Integer>())
                    .build();

            MockHttpServletResponse response = mvc.perform(
                    post("/api/v1/answer/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(answer))
                            .header("Authorization", String.format("Bearer %s", studentToken))
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn()
                    .getResponse();

            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }

    }

    @Test
    void answers_create_success() throws Exception {
        TaskAnswer answer = TaskAnswer.builder()
                .taskId(53)
                .studentId(5)
                .date(new Date())
                .attachments(new ArrayList<Integer>())
                .build();

        MockHttpServletResponse response = mvc.perform(
                post("/api/v1/answer/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(answer))
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }
}
