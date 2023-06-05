package com.oleg.educationalplatform.taskmodule.attachment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Integer> {
    @Query(value = "SELECT id FROM attachment WHERE post_id = ?1", nativeQuery = true)
    List<Integer> getIdsByPostId(Integer taskId);
}
