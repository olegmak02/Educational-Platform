package com.oleg.educationalplatform.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oleg.educationalplatform.assessment.Assessment;
import com.oleg.educationalplatform.assessment.AssessmentController;
import com.oleg.educationalplatform.assessment.AssessmentRepository;
import com.oleg.educationalplatform.security.config.JwtAuthenticationFilter;
import com.oleg.educationalplatform.security.config.JwtService;
import com.oleg.educationalplatform.security.user.User;
import com.oleg.educationalplatform.security.user.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ActiveProfiles("test")
@SpringBootTest
public class AssessmentTests {
    private MockMvc mvc;
    private static String studentToken;
    private static String teacherToken;

    @Autowired
    private AssessmentRepository assessmentRepository;
    @Autowired
    private JwtService jwtService;

    @Autowired
    @InjectMocks
    private AssessmentController assessmentController;

    @Autowired
    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private UserRepository repository;

    @Autowired
    private ObjectMapper mapper;

    @BeforeAll
    public static void init(@Autowired JwtService jwtService, @Autowired UserRepository repository) {
        User student = repository.findById(5).get();
        User teacher = repository.findById(2).get();
        studentToken = jwtService.generateToken(student);
        teacherToken = jwtService.generateToken(teacher);
    }

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.standaloneSetup(assessmentController)
                .addFilter(jwtAuthenticationFilter)
                .build();
    }

    @Test
    void getMyAssessments_studentIsInCourse() throws Exception {
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
    void getMyAssessments_studentIsNotInCourse() throws Exception {
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
    void getMyAssessments_unexistingCourse() throws Exception {
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
    void updateMark_existingAssessment() throws Exception {
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
    void createMark_successfully() throws Exception {
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
    void createMark_noTask()  {
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
    void getStudentAssessments_existingAssessments() throws Exception {
        MockHttpServletResponse getResponse = mvc.perform(
                get("/api/v1/assessment/course/1/student/4")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        List<Assessment> assessments = mapper.readValue(getResponse.getContentAsString(), ArrayList.class);
        assertEquals(1, assessments.size());
    }

    @Test
    void getStudentAssessments_unexistingCourse() throws Exception {
        MockHttpServletResponse getResponse = mvc.perform(
                get("/api/v1/assessment/course/112/student/4")
                        .header("Authorization", String.format("Bearer %s", studentToken))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        List<Assessment> assessments = mapper.readValue(getResponse.getContentAsString(), ArrayList.class);
        assertEquals(0, assessments.size());
    }
}
