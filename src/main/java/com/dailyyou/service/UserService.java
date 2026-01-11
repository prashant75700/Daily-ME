package com.dailyyou.service;

import com.dailyyou.entity.User;
import com.dailyyou.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username '" + user.getUsername() + "' is already taken.");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("An account with email '" + user.getEmail() + "' already exists.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Hash the security answer too, so it's private
        if (user.getSecurityAnswer() != null) {
            user.setSecurityAnswer(passwordEncoder.encode(user.getSecurityAnswer().toLowerCase().trim()));
        }
        
        userRepository.save(user);
    }
    
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
