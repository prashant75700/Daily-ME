package com.dailyyou.service;

import com.dailyyou.dto.EntryRequestDTO;
import com.dailyyou.dto.MoodStatsDTO;
import com.dailyyou.entity.DiaryEntry;
import com.dailyyou.entity.Mood;
import com.dailyyou.entity.User;
import com.dailyyou.repository.EntryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DiaryService {

    private final EntryRepository entryRepository;
    private final Path rootLocation = Paths.get("uploads");

    public DiaryService(EntryRepository entryRepository) {
        this.entryRepository = entryRepository;
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage", e);
        }
    }

    @Transactional
    public void saveEntry(User user, EntryRequestDTO dto) {
        DiaryEntry entry = new DiaryEntry();
        entry.setUser(user);
        entry.setContent(dto.getContent());
        entry.setDate(dto.getDate());
        entry.setMood(dto.getMood());

        MultipartFile image = dto.getImage();
        if (image != null && !image.isEmpty()) {
            String filename = StringUtils.cleanPath(image.getOriginalFilename());
            String extension = StringUtils.getFilenameExtension(filename);
            String storedFileName = UUID.randomUUID().toString() + (extension != null ? "." + extension : "");

            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, this.rootLocation.resolve(storedFileName), StandardCopyOption.REPLACE_EXISTING);
                entry.setImagePath("/uploads/" + storedFileName);
            } catch (IOException e) {
                throw new RuntimeException("Failed to store file " + filename, e);
            }
        }

        entryRepository.save(entry);
    }
    
    public List<DiaryEntry> getUserEntries(User user) {
        return entryRepository.findByUserOrderByDateDesc(user);
    }

    public List<MoodStatsDTO> getMoodStats(User user, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        List<DiaryEntry> entries = entryRepository.findByUserAndDateBetween(user, start, end);

        Map<Mood, Long> counts = entries.stream()
                .collect(Collectors.groupingBy(DiaryEntry::getMood, Collectors.counting()));

        return counts.entrySet().stream()
                .map(entry -> new MoodStatsDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}
