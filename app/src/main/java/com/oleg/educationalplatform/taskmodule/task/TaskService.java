package com.oleg.educationalplatform.taskmodule.task;

import com.oleg.educationalplatform.security.user.User;
import com.oleg.educationalplatform.taskmodule.attachment.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final AttachmentRepository attachmentRepository;

    public Task getTaskById(Integer id) {
        Task task = taskRepository.findById(id)
                .orElseThrow();

        task.setAttachments(attachmentRepository.getIdsByPostId(task.getId()));
        return task;
    }

    public Task createTask(Task task) {
        if (task.getCreationTime() == null) {
            task.setCreationTime(new Date());
        }
        return taskRepository.save(task);
    }

    public List<Task> getTaskByCourse(Integer courseId) {
        return taskRepository.findAllByCourseId(courseId);
    }

    public List<Task> getSubmittedTaskByCourse(Integer courseId) {
        User currentUser = (User) (SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return taskRepository.findSubmittedByCourseId(courseId, currentUser.getId());
    }

    public List<Task> getIncompliteTaskByCourse(Integer courseId) {
        User currentUser = (User) (SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return taskRepository.findIncompliteByCourseId(courseId, currentUser.getId());
    }

    public List<Task> getTaskByCourseAndTitle(Integer courseId, String title) {
        return taskRepository.findTasksByCourseIdAndTitle(courseId, title);
    }
}
