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

        if (user.getPassword().length() < 5) {
            throw new IllegalArgumentException("Password must be at least 5 characters long.");
        }
        if (!user.getPassword().matches(".*[A-Z].*")) {
             throw new IllegalArgumentException("Password must contain at least one uppercase letter.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Hash the security answer too, so it's private
        // Hash the security answer (Case Sensitive now, so "Appy" != "appy")
        if (user.getSecurityAnswer() != null) {
            user.setSecurityAnswer(passwordEncoder.encode(user.getSecurityAnswer().trim()));
        }
        
        userRepository.save(user);
    }
    
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
