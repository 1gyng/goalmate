package com.gm.goalmate.domain.common;

import com.gm.goalmate.domain.goal.GoalType;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public record DateRange(LocalDate startDate, LocalDate dueDate) {

    public DateRange {
        validate(startDate, dueDate);
    }

    public static DateRange of(GoalType type, LocalDate baseDate) {
        return new DateRange(
                determineStartDate(type, baseDate),
                determineDueDate(type, baseDate)
        );
    }

    private static void validate(LocalDate startDate, LocalDate dueDate) {
        if (dueDate.isBefore(startDate)) {
            throw new IllegalArgumentException("종료일은 시작일보다 빠를 수 없습니다.");
        }
    }

    private static LocalDate determineStartDate(GoalType type, LocalDate baseDate) {
        return switch (type) {
            case DAILY -> baseDate;
            case WEEKLY -> baseDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
            case MONTHLY -> baseDate.with(TemporalAdjusters.firstDayOfMonth());
            case YEARLY -> baseDate.with(TemporalAdjusters.firstDayOfYear());
        };
    }

    private static LocalDate determineDueDate(GoalType type, LocalDate baseDate) {
        return switch (type) {
            case DAILY -> baseDate;
            case WEEKLY -> baseDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
            case MONTHLY -> baseDate.with(TemporalAdjusters.lastDayOfMonth());
            case YEARLY -> baseDate.with(TemporalAdjusters.lastDayOfYear());
        };
    }
}
