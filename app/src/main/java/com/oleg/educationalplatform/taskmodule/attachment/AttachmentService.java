package com.oleg.educationalplatform.taskmodule.attachment;

import com.oleg.educationalplatform.security.user.Role;
import com.oleg.educationalplatform.security.user.User;
import com.oleg.educationalplatform.taskmodule.taskanswer.TaskAnswer;
import com.oleg.educationalplatform.taskmodule.taskanswer.TaskAnswerRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Data
@RequiredArgsConstructor
@Service
public class AttachmentService {
    private static final String uploadDir = "/files/";

    private final AttachmentRepository repository;
    private final TaskAnswerRepository taskAnswerRepository;
    @Autowired
    private HttpServletRequest request;

    public Attachment getAttachmentById(Integer id) {
        Attachment attachment = repository.findById(id)
                .orElseThrow();

        Optional<TaskAnswer> taskAnswer = taskAnswerRepository.findById(attachment.getPostId());

        if (taskAnswer.isPresent()) {
            User currentUser = (User) (SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            if (!currentUser.getId().equals(taskAnswer.get().getStudentId()) && currentUser.getRole() == Role.STUDENT) {
                throw new RuntimeException("The answer belongs to another student");
            }
        }

        return attachment;
    }

    public Integer saveAttachment(Attachment attachment) {
        if (!attachment.getFile().isEmpty()) {
            try {
                String fullPath = request.getServletContext().getRealPath(uploadDir);
                if (!new File(fullPath).exists()) {
                    new File(fullPath).mkdir();
                }

                File destination = new File(fullPath + attachment.getFile().getOriginalFilename());

                attachment.getFile().transferTo(destination);

                attachment.setPath(destination.getPath());
                Attachment res = repository.save(attachment);
                return res.getId();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public List<Integer> getAllByPostId(Integer id) {
        return repository.getIdsByPostId(id);
    }

}
