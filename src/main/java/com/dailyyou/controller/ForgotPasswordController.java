package com.dailyyou.controller;

import com.dailyyou.entity.PasswordResetToken;
import com.dailyyou.entity.User;
import com.dailyyou.repository.PasswordResetTokenRepository;
import com.dailyyou.repository.UserRepository;
import com.dailyyou.service.EmailService;
import com.dailyyou.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
public class ForgotPasswordController {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public ForgotPasswordController(UserRepository userRepository, 
                                  PasswordResetTokenRepository tokenRepository,
                                  EmailService emailService,
                                  PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "auth/forgot_password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, Model model) {
        User user = userRepository.findByEmail(email).orElse(null);
        
        if (user != null) {
            String token = UUID.randomUUID().toString();
            PasswordResetToken myToken = new PasswordResetToken(token, user);
            tokenRepository.save(myToken);
            
            emailService.sendPasswordResetEmail(user.getEmail(), token);
        }
        
        // Always return success message for security (don't reveal if email exists)
        model.addAttribute("message", "If an account exists for that email, we have sent a password reset link.");
        return "auth/forgot_password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam String token, Model model) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token).orElse(null);
        
        if (resetToken == null || resetToken.isExpired()) {
            model.addAttribute("error", "Invalid or expired token");
            return "auth/reset_password";
        }
        
        model.addAttribute("token", token);
        return "auth/reset_password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam String token, @RequestParam String password, Model model) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token).orElse(null);
        
        if (resetToken == null || resetToken.isExpired()) {
            model.addAttribute("error", "Invalid or expired token");
            return "auth/reset_password";
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user); // Updates user password
        
        tokenRepository.delete(resetToken); // Consume token
        
        return "redirect:/login?resetSuccess";
    }
}
