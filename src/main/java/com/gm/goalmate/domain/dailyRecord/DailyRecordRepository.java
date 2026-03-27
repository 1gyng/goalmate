package com.gm.goalmate.domain.dailyRecord;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface DailyRecordRepository extends JpaRepository<DailyRecord, Long> {
    boolean existsByRecordDateAndUser_UserId(LocalDate recordDate, Long userId);

    List<DailyRecord> findAllByUser_userIdAndRecordDateBetween(Long userUserId, LocalDate recordDateAfter, LocalDate recordDateBefore);

    @Query(value = "SELECT d.emotion AS name, COUNT(*) AS recorded_count FROM daily_record d WHERE d.user_id = :userId AND d.record_date BETWEEN :first AND :last GROUP BY d.emotion", nativeQuery = true)
    List<EmotionCount> countEmotion(Long userId, LocalDate first, LocalDate last);
}
