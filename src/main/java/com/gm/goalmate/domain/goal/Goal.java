package com.gm.goalmate.domain.goal;

import com.gm.goalmate.domain.user.User;
import com.gm.goalmate.dto.GoalRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Goal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long goalId;

    @Column(nullable = false)
    private String task;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalResult result = GoalResult.NONE;

    private LocalDate resultDate;

    @Column(nullable = false)
    @Temporal(value = TemporalType.DATE)
    private LocalDate startDate; //시작일

    @Column(nullable = false)
    @Temporal(value = TemporalType.DATE)
    private LocalDate dueDate; //종료일

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Goal(String task, GoalType type, LocalDate startDate, LocalDate dueDate, User user) {
        validateDateRange(startDate, dueDate);

        this.task = task;
        this.type = type;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.user = user;
    }

    public void update(GoalRequest.Update request) {
        this.type = request.getType();
        this.task = request.getTask();
        this.startDate = request.getStartDate();
        this.dueDate = request.getDueDate();
    }

    public void updateResult(GoalRequest.UpdateResult request, LocalDate date) {
        GoalResult result = request.getResult();
        validateResult(result);
        this.result = result;
        this.resultDate = date;
    }

    private void validateDateRange(LocalDate startDate, LocalDate dueDate) {
        if (dueDate.isBefore(startDate)) {
            throw new IllegalArgumentException("종료일은 시작일보다 빠를 수 없습니다.");
        }
    }

    private void validateResult(GoalResult result) {
        if (result != GoalResult.SUCCESS && result != GoalResult.FAILED) {
            throw new IllegalArgumentException("달성 또는 미달성만 가능합니다.");
        }
    }
}
