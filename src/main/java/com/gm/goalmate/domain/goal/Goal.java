package com.gm.goalmate.domain.goal;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Goal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long goalNum;

    private Long writerId;

    private String task;

    private GoalType type;

    @Enumerated(EnumType.STRING)
    private GoalStatus status;

    @Temporal(value = TemporalType.DATE)
    private Date startDate; //시작일

    @Temporal(value = TemporalType.DATE)
    private Date dueDate; //마감일
}
