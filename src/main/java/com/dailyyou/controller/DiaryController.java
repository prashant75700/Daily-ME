package com.dailyyou.controller;

import com.dailyyou.dto.EntryRequestDTO;
import com.dailyyou.dto.MoodStatsDTO;
import com.dailyyou.entity.User;
import com.dailyyou.service.DiaryService;
import com.dailyyou.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/diary")
public class DiaryController {

    private final DiaryService diaryService;
    private final UserService userService;

    public DiaryController(DiaryService diaryService, UserService userService) {
        this.diaryService = diaryService;
        this.userService = userService;
    }

    @GetMapping("/form")
    public String showForm(Model model) {
        EntryRequestDTO dto = new EntryRequestDTO();
        dto.setDate(LocalDate.now()); // Default to today
        model.addAttribute("entryDto", dto);
        return "diary/form";
    }

    @PostMapping("/save")
    public String saveEntry(@ModelAttribute("entryDto") @Valid EntryRequestDTO entryDto,
                            BindingResult result,
                            Principal principal,
                            Model model) {
        
        if (result.hasErrors()) {
            return "diary/form";
        }

        User user = userService.findByUsername(principal.getName());
        diaryService.saveEntry(user, entryDto);

        return "redirect:/dashboard";
    }

    @GetMapping("/calendar")
    public String showCalendar(Model model, 
                               Principal principal, 
                               @RequestParam(defaultValue = "0") int year, 
                               @RequestParam(defaultValue = "0") int month) {
        
        if (year == 0) year = LocalDate.now().getYear();
        if (month == 0) month = LocalDate.now().getMonthValue();

        User user = userService.findByUsername(principal.getName());
        List<MoodStatsDTO> stats = diaryService.getMoodStats(user, year, month);

        model.addAttribute("stats", stats);
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        
        return "diary/calendar"; // You'll need to create this template
    }
}
