package com.oleg.educationalplatform.security.user;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Data
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findUserById(Integer id) {
        return userRepository.findById(id).orElseThrow();
    }

    public List<User> findAllTeachers() {
        return userRepository.findAllTeachers();
    }

    public List<User> findAllStudents() {
        return userRepository.findAllStudents();
    }

    public User getMyInfo() {
        return (User) (SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    public User update(User user) {
        User updateUser = findUserById(user.getId());
        updateUser.setUsername(user.getUsername());
        updateUser.setFirstname(user.getFirstname());
        updateUser.setGroupName(user.getGroupName());
        updateUser.setLastname(user.getLastname());
        return userRepository.save(updateUser);
    }

    public void deleteUserById(Integer id) {
        userRepository.deleteById(id);
    }
}
