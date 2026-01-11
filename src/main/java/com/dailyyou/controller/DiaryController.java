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

import org.springframework.web.bind.annotation.PathVariable;
import java.time.YearMonth;
import com.dailyyou.entity.DiaryEntry;
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
    public String showForm(Model model, @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate date) {
        EntryRequestDTO dto = new EntryRequestDTO();
        dto.setDate(date != null ? date : LocalDate.now());
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

    @PostMapping("/delete/{id}")
    public String deleteEntry(@PathVariable Long id, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        try {
            diaryService.deleteEntry(id, user);
        } catch (SecurityException e) {
            // Log error or handle
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/edit/{id}")
    public String editEntry(@PathVariable Long id, Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        DiaryEntry entry = diaryService.getEntry(id, user);
        
        EntryRequestDTO dto = new EntryRequestDTO();
        dto.setId(entry.getId());
        dto.setContent(entry.getContent());
        dto.setMood(entry.getMood());
        dto.setDate(entry.getDate());
        
        model.addAttribute("entryDto", dto);
        return "diary/form";
    }

    @GetMapping("/calendar")
    public String showCalendar(Model model, 
                               Principal principal, 
                               @RequestParam(defaultValue = "0") int year, 
                               @RequestParam(defaultValue = "0") int month) {
        
        if (year == 0) year = LocalDate.now().getYear();
        if (month == 0) month = LocalDate.now().getMonthValue();

        User user = userService.findByUsername(principal.getName());
        
        // 1. Get all entries for the month
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();
        List<DiaryEntry> entries = diaryService.getUserEntries(user).stream()
            .filter(e -> !e.getDate().isBefore(start) && !e.getDate().isAfter(end))
            .toList();

        // 2. Map entries by Day of Month for easy lookup
        java.util.Map<Integer, DiaryEntry> entryMap = entries.stream()
            .collect(java.util.stream.Collectors.toMap(e -> e.getDate().getDayOfMonth(), e -> e, (e1, e2) -> e1)); // Keep first if duplicates

        // 3. Create Calendar Grid (List of Integers/Nulls)
        List<Integer> days = new java.util.ArrayList<>();
        int daysInMonth = yearMonth.lengthOfMonth();
        int firstDayOfWeek = start.getDayOfWeek().getValue(); // 1=Mon, 7=Sun
        
        // Add empty slots for days before the 1st (adjusting so Sunday is last column usually, or standard calendar layout)
        // Let's assume Mon-Sun week.
        // If 1st is Mon (1), 0 empties. If Tue (2), 1 empty.
        // Formula: (firstDayOfWeek - 1) % 7
        for (int i = 1; i < firstDayOfWeek; i++) {
            days.add(null);
        }
        
        // Add actual days
        for (int i = 1; i <= daysInMonth; i++) {
            days.add(i);
        }

        model.addAttribute("days", days);
        model.addAttribute("entryMap", entryMap);
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("monthName", java.time.Month.of(month).name());
        
        return "diary/calendar";
    }
}
