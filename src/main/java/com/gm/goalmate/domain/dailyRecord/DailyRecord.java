package com.gm.goalmate.domain.dailyRecord;

import com.gm.goalmate.domain.user.User;
import com.gm.goalmate.dto.DailyRecordRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "daily_record",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_daily_record_user_id_record_date", columnNames = {"user_id", "record_date"})
        }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DailyRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Emotion emotion;

    @Column(nullable = false, length = 50)
    private String content;

    @Column(nullable = false)
    private LocalDate recordDate;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public DailyRecord(Emotion emotion, String content, LocalDate recordDate, User user, LocalDate referenceDate) {
        validateRecordDate(recordDate, referenceDate);

        this.emotion = emotion;
        this.content = content;
        this.recordDate = recordDate;
        this.user = user;
    }

    private void validateRecordDate(LocalDate recordDate, LocalDate referenceDate) {
        if (recordDate.isAfter(referenceDate)) {
            throw new IllegalArgumentException("미래 날짜에 대한 기록은 할 수 없습니다.");
        }
    }

    public void update(DailyRecordRequest.Update request) {
        this.emotion = request.getEmotion();
        this.content = request.getContent();
    }
}
