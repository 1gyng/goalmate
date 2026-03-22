package com.gm.goalmate.service;

import com.gm.goalmate.domain.common.DateRange;
import com.gm.goalmate.domain.dailyRecord.DailyRecordRepository;
import com.gm.goalmate.domain.dailyRecord.Emotion;
import com.gm.goalmate.domain.dailyRecord.EmotionCount;
import com.gm.goalmate.domain.goal.EmotionSuccessRate;
import com.gm.goalmate.domain.goal.GoalRepository;
import com.gm.goalmate.domain.goal.GoalType;
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
        DateRange period = DateRange.of(GoalType.MONTHLY, lastMonth);
        int totalDays = lastMonth.lengthOfMonth();

        List<EmotionCount> counts = dailyRecordRepository.countEmotion(userId, period.startDate(), period.dueDate());
        List<EmotionSuccessRate> rates = goalRepository.findSuccessRateGroupByEmotion(userId, period.startDate(), period.dueDate());

        long recordCount = counts.stream().mapToLong(EmotionCount::getCount).sum();

        Map<String, StatResponse.EmotionStat> statMap = buildEmotionStatMap(counts, rates);
        updateUnrecordedCount(statMap, totalDays, recordCount);

        return new ArrayList<>(statMap.values());
    }

    private Map<String, StatResponse.EmotionStat> buildEmotionStatMap(List<EmotionCount> counts, List<EmotionSuccessRate> rates) {
        Map<String, StatResponse.EmotionStat> statMap = new LinkedHashMap<>();

        for (Emotion emotion : Emotion.values()) {
            statMap.put(emotion.name(), new StatResponse.EmotionStat(emotion.name(), emotion.getDescription(), 0L, 0.0));
        }
        statMap.put("UNRECORDED", new StatResponse.EmotionStat("UNRECORDED", "기록 없음", 0L, 0.0));


        counts.forEach(c -> {
            statMap.computeIfPresent(c.getName(), (key, existing) ->
                    new StatResponse.EmotionStat(key, existing.getDescription(), c.getCount(), existing.getSuccessRate()));
        });

        rates.forEach(r -> {
            statMap.computeIfPresent(r.getName(), (key, existing) ->
                    new StatResponse.EmotionStat(key, existing.getDescription(), existing.getCount(), r.getSuccessRate()));
        });

        return statMap;
    }

    private void updateUnrecordedCount(Map<String, StatResponse.EmotionStat> statMap, int totalDays, long recordCount) {
        long unrecordedCount = (long) totalDays - recordCount;

        statMap.computeIfPresent("UNRECORDED", (key, existing) ->
                new StatResponse.EmotionStat(key, existing.getDescription(), unrecordedCount, existing.getSuccessRate())
        );
    }
}
