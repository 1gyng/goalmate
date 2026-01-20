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
    private GoalStatus status;

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
    public Goal(String task, GoalType type, LocalDate startDate, LocalDate dueDate, User user, LocalDate today) {
        validateDateRange(startDate, dueDate);

        this.task = task;
        this.type = type;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.user = user;
        this.status = determineStatus(today);
    }

    public void update(GoalRequest.Update request) {
        this.type = request.getType();
        this.task = request.getTask();
        this.startDate = request.getStartDate();
        this.dueDate = request.getDueDate();
    }

    private void validateDateRange(LocalDate startDate, LocalDate dueDate) {
        if(dueDate.isBefore(startDate)) {
            throw new IllegalArgumentException("종료일은 시작일보다 빠를 수 없습니다.");
        }
    }

    public GoalStatus determineStatus(LocalDate today) {
        if(today.isBefore(this.startDate)) {
            return GoalStatus.TODO;
        } else if (today.isAfter(this.dueDate)) {
            return GoalStatus.COMPLETED;
        } else {
            return GoalStatus.IN_PROGRESS;
        }
    }
}
