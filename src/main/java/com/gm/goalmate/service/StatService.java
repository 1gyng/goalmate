package com.gm.goalmate.service;

import com.gm.goalmate.domain.goal.DateRange;
import com.gm.goalmate.domain.dailyRecord.DailyRecordRepository;
import com.gm.goalmate.domain.dailyRecord.Emotion;
import com.gm.goalmate.domain.dailyRecord.EmotionCount;
import com.gm.goalmate.domain.goal.EmotionSuccessRate;
import com.gm.goalmate.domain.goal.GoalRepository;
import com.gm.goalmate.domain.goal.GoalType;
import com.gm.goalmate.domain.goal.TypeSuccessRate;
import com.gm.goalmate.dto.StatResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class StatService {
    private final GoalRepository goalRepository;
    private final DailyRecordRepository dailyRecordRepository;

    public StatService(GoalRepository goalRepository, DailyRecordRepository dailyRecordRepository) {
        this.goalRepository = goalRepository;
        this.dailyRecordRepository = dailyRecordRepository;
    }

    public List<StatResponse.EmotionStat> getMonthlyEmotionStat(Long userId) {
        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        DateRange period = DateRange.ofType(GoalType.MONTHLY, lastMonth);
        int totalDays = lastMonth.lengthOfMonth();

        List<EmotionCount> counts = dailyRecordRepository.countEmotion(userId, period.startDate(), period.dueDate());
        List<EmotionSuccessRate> rates = goalRepository.findSuccessRateGroupByEmotion(userId, period.startDate(), period.dueDate());

        Map<Emotion, StatResponse.EmotionStat> statMap = buildEmotionStatMap(counts, rates);

        List<StatResponse.EmotionStat> finalStat = new ArrayList<>(statMap.values());
        finalStat.add(createUnrecordedCount(counts, rates, totalDays));

        return finalStat;
    }

    public List<StatResponse.SuccessRateStat> getMonthlySuccessRateStat(Long userId) {
        DateRange period = DateRange.ofType(GoalType.MONTHLY, LocalDate.now());
        List<TypeSuccessRate> rates = goalRepository.findSuccessRateGroupByType(userId, period.startDate(), period.dueDate());

        Map<GoalType, StatResponse.SuccessRateStat> statMap = createSuccessRateStatMap(rates);
        return new ArrayList<>(statMap.values());
    }

    private Map<GoalType, StatResponse.SuccessRateStat> createSuccessRateStatMap(List<TypeSuccessRate> rates) {
        Map<GoalType, StatResponse.SuccessRateStat> statMap = new EnumMap<>(GoalType.class);
        for (GoalType type : GoalType.values()) {
            statMap.put(type, StatResponse.SuccessRateStat.init(type));
        }

        rates.forEach(r -> {
            if(r.getType() != null) {
                statMap.put(r.getType(), new StatResponse.SuccessRateStat(r.getType(), r.getSuccessRate(), r.getSuccessCount(), r.getTotalCount()));
            }
        });

        return statMap;
    }

    private Map<Emotion, StatResponse.EmotionStat> buildEmotionStatMap(List<EmotionCount> counts, List<EmotionSuccessRate> rates) {
        Map<Emotion, StatResponse.EmotionStat> statMap = new EnumMap<>(Emotion.class);

        for (Emotion emotion : Emotion.values()) {
            statMap.put(emotion, StatResponse.EmotionStat.init(emotion));
        }

        counts.forEach(c -> {
            Emotion emotion = Emotion.valueOf(c.getName());
            statMap.computeIfPresent(emotion, (key, existing) ->
                    new StatResponse.EmotionStat(key.name(), existing.getDescription(), c.getRecordedCount(), existing.getSuccessRate()));
        });

        rates.forEach(r -> {
            if (StatResponse.UNRECORDED_KEY.equals(r.getName())) {
                return;
            }

            Emotion emotion = Emotion.valueOf(r.getName());
            statMap.computeIfPresent(emotion, (key, existing) ->
                    new StatResponse.EmotionStat(key.name(), existing.getDescription(), existing.getRecordedCount(), r.getSuccessRate()));
        });

        return statMap;
    }

    private StatResponse.EmotionStat createUnrecordedCount(List<EmotionCount> counts, List<EmotionSuccessRate> rates, int totalDays) {
        long recordedCount = counts.stream().mapToLong(EmotionCount::getRecordedCount).sum();
        long unrecordedCount = (long) totalDays - recordedCount;
        double rate = rates.stream().filter(r -> StatResponse.UNRECORDED_KEY.equals(r.getName())).map(EmotionSuccessRate::getSuccessRate).findFirst().orElse(0.0);

        return new StatResponse.EmotionStat(StatResponse.UNRECORDED_KEY, StatResponse.UNRECORDED_DESCRIPTION, unrecordedCount, rate);
    }

}