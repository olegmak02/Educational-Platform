package com.oleg.educationalplatform.course.studentcourse;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "student_course")
public class StudentCourse {
    @Id
    @GeneratedValue
    private Integer id;

    private Integer studentId;
    private Integer courseId;
    private Integer mark;
}
