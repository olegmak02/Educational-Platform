package com.oleg.educationalplatform.taskmodule.material;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MaterialRespository extends JpaRepository<Material, Integer> {
    @Query(value = "SELECT * FROM material WHERE course_id = ?1", nativeQuery = true)
    Material findByCourseId(Integer courseId);
}
