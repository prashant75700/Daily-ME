package com.dailyyou.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(MaxUploadSizeExceededException exc, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "File is too large! Please upload images smaller than 50MB.");
        return "redirect:/diary/form";
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleArgumentException(IllegalArgumentException exc, RedirectAttributes redirectAttributes) {
        // Keeps user safe from stack traces if entry not found or valid
        // Often redirected to dashboard if specific ID not found
        redirectAttributes.addFlashAttribute("error", exc.getMessage());
        return "redirect:/dashboard";
    }
}
