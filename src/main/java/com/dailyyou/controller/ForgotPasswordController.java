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
        
        if (user == null) {
             // Security: Don't reveal if user exists. Just reload page or show same generic message? 
             // Ideally we should show "If account exists..." but for this flow we need to show the question.
             // So we must reveal if user exists or not? No, we can fake it or just say "User not found".
             // For a portfolio, "User not found" is acceptable simplicity.
             model.addAttribute("error", "No account found with that email.");
             return "auth/forgot_password";
        }
        
        if (user.getSecurityQuestion() == null) {
             model.addAttribute("error", "This account has no security question set. Contact support.");
             return "auth/forgot_password";
        }

        // Redirect to verification page with userId (hidden)
        model.addAttribute("userId", user.getId());
        model.addAttribute("question", user.getSecurityQuestion().getQuestion());
        return "auth/verify_security";
    }

    @PostMapping("/verify-security")
    public String verifySecurity(@RequestParam Long userId, @RequestParam String answer, Model model) {
        User user = userRepository.findById(userId).orElse(null);
        
        if (user == null) {
            return "redirect:/login"; // Should not happen
        }

        // Check Answer
        if (passwordEncoder.matches(answer.toLowerCase().trim(), user.getSecurityAnswer())) {
            // Success! Generate token and let them reset
            String token = UUID.randomUUID().toString();
            PasswordResetToken myToken = new PasswordResetToken(token, user);
            tokenRepository.save(myToken);
            
            // Redirect to reset password page directly with token
            return "redirect:/reset-password?token=" + token;
        } else {
             model.addAttribute("userId", userId);
             model.addAttribute("question", user.getSecurityQuestion().getQuestion());
             model.addAttribute("error", "Incorrect answer.");
             return "auth/verify_security";
        }
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
