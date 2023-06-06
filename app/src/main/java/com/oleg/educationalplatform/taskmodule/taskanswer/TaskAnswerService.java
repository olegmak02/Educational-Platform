package com.oleg.educationalplatform.taskmodule.taskanswer;

import com.oleg.educationalplatform.security.user.User;
import com.oleg.educationalplatform.taskmodule.attachment.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskAnswerService {

    private final TaskAnswerRepository taskAnswerRepository;
    private final AttachmentRepository attachmentRepository;


    public List<TaskAnswer> getAnswersByTaskId(Integer taskId) {
        return taskAnswerRepository.getAnswersByTaskId(taskId);
    }

    public TaskAnswer createTaskAnswer(TaskAnswer answer) {
        answer.setDate(new Date());
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (answer.getDate() == null) {
            answer.setDate(new Date());
        }
        if (answer.getStudentId() == null) {
            answer.setStudentId(user.getId());
        }
        return taskAnswerRepository.save(answer);
    }

    public TaskAnswer getAnswerByTaskIdAndUser(Integer taskId, Integer studentId) {
        TaskAnswer answer = taskAnswerRepository.getAnswersByTaskIdAndUser(studentId, taskId);
        answer.setAttachments(attachmentRepository.getIdsByPostId(answer.getId()));
        return answer;
    }

    public TaskAnswer getAnswerByTaskIdAndCurrentUser(Integer taskId) {
        User currentUser = (User) (SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return taskAnswerRepository.getAnswersByTaskIdAndUser(currentUser.getId(), taskId);
    }
}

