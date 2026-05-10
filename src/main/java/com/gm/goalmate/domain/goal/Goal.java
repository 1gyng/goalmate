package com.gm.goalmate.domain.goal;

import com.gm.goalmate.domain.user.User;
import com.gm.goalmate.dto.GoalRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "goal",
        indexes = {
                @Index(name = "idx_goal_user_id_type_start_date", columnList = "user_id, type, start_date")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Goal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long goalId;

    @Column(nullable = false, length = 20)
    private String task;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalResult result = GoalResult.NONE;

    private LocalDate resultDate;

    @Embedded
    private DateRange dateRange;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Goal(String task, GoalType type, LocalDate startDate, LocalDate dueDate, User user) {
        this.task = task;
        this.type = type;
        this.dateRange = DateRange.of(startDate, dueDate);
        this.user = user;
    }

    public void update(GoalRequest.Update request) {
        this.type = request.getType();
        this.task = request.getTask();
        this.dateRange = DateRange.of(request.getStartDate(), request.getDueDate());
    }

    public void updateResult(GoalRequest.UpdateResult request, LocalDate date) {
        GoalResult result = request.getResult();
        validateResult(result);
        this.result = result;
        this.resultDate = date;
    }

    private void validateResult(GoalResult result) {
        if (result != GoalResult.SUCCESS && result != GoalResult.FAILED) {
            throw new IllegalArgumentException("달성 또는 미달성만 가능합니다.");
        }
    }
}
