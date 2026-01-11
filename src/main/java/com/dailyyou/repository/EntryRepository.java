package com.dailyyou.repository;

import com.dailyyou.entity.DiaryEntry;
import com.dailyyou.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface EntryRepository extends JpaRepository<DiaryEntry, Long> {
    
    // For list view
    List<DiaryEntry> findByUserOrderByDateDesc(User user);

    // For calendar/pixel view
    List<DiaryEntry> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);
}
