package com.gm.goalmate.domain.dailyRecord;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DailyRecordRepository extends JpaRepository<DailyRecord, Long> {
    boolean existsByRecordDateAndUser_UserId(LocalDate recordDate, Long userId);

    List<DailyRecord> findAllByUser_userIdAndRecordDateBetween(Long userUserId, LocalDate recordDateAfter, LocalDate recordDateBefore);
}
