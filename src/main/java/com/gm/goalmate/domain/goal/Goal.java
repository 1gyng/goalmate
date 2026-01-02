package com.gm.goalmate.domain.goal;

import com.gm.goalmate.domain.user.User;
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
@Builder
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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public void updateGoal(GoalType type, String task, LocalDate startDate, LocalDate dueDate) {
        this.type = type;
        this.task = task;
        this.startDate = startDate;
        this.dueDate = dueDate;
    }
}
