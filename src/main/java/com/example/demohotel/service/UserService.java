package com.example.demohotel.service;

import com.example.demohotel.dto.CreateUserRequest;
import com.example.demohotel.dto.ResponseDTO;
import com.example.demohotel.entity.User;
import com.example.demohotel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long userId) {
        return userRepository.findByUserId(userId);
    }

    public User createUser(CreateUserRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setFullName(request.getFullName());

        return userRepository.save(user);
    }

    public User updateUser(Long userId, CreateUserRequest request) {
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            return null;
        }

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setFullName(request.getFullName());

        return userRepository.save(user);
    }

    public ResponseDTO deleteUser(Long userId) {
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            return new ResponseDTO(false, "User not found");
        }

        user.setStatus(false);
        userRepository.save(user);
        return new ResponseDTO(true, "User deleted successfully");
    }
}
