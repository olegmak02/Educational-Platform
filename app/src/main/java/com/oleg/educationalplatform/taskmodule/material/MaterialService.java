package com.oleg.educationalplatform.taskmodule.material;

import com.oleg.educationalplatform.taskmodule.attachment.AttachmentRepository;
import com.oleg.educationalplatform.taskmodule.task.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaterialService {

    private final AttachmentRepository attachmentRepository;
    private final MaterialRespository materialRespository;

    public List<Integer> getFilesByCourseId(Integer courseId) {
        Material material = materialRespository.findByCourseId(courseId);
        return attachmentRepository.getIdsByPostId(material.getId());
    }

    public Material create(Material material) {
        return materialRespository.save(material);
    }

    public Integer getIdByCourseId(Integer courseId) {
        Material material = materialRespository.findByCourseId(courseId);
        return material.getId();
    }
}
