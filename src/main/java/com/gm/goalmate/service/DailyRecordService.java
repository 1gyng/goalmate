package com.gm.goalmate.service;

import com.gm.goalmate.domain.dailyRecord.DailyRecord;
import com.gm.goalmate.domain.dailyRecord.DailyRecordRepository;
import com.gm.goalmate.domain.user.User;
import com.gm.goalmate.domain.user.UserRepository;
import com.gm.goalmate.dto.DailyRecordRequest;
import com.gm.goalmate.dto.DailyRecordResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

@Service
public class DailyRecordService {

    private final UserRepository userRepository;
    private final DailyRecordRepository dailyRecordRepository;

    public DailyRecordService(UserRepository userRepository, DailyRecordRepository dailyRecordRepository) {
        this.userRepository = userRepository;
        this.dailyRecordRepository = dailyRecordRepository;
    }

    @Transactional
    public Long addDailyRecord(DailyRecordRequest.Add request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (dailyRecordRepository.existsByRecordDateAndUser_UserId(request.getRecordDate(), userId)) {
            throw new IllegalStateException("이미 해당 날짜(" + request.getRecordDate() + ")의 기록을 완료했습니다.");
        }

        DailyRecord dailyRecord = DailyRecord.builder()
                .emotion(request.getEmotion())
                .content(request.getContent())
                .recordDate(request.getRecordDate())
                .user(user)
                .referenceDate(LocalDate.now())
                .build();

        return dailyRecordRepository.save(dailyRecord).getRecordId();
    }

    public List<DailyRecordResponse.RecordCheck> getRecordChecksByYear(Long userId, int year) {
        LocalDate start = Year.of(year - 1).atMonth(12).atDay(1);
        LocalDate end = Year.of(year).atMonth(12).atEndOfMonth();

        List<DailyRecord> dailyRecords = dailyRecordRepository.findAllByUser_userIdAndRecordDateBetween(userId, start, end);

        return dailyRecords.stream()
                .map(dailyRecord -> DailyRecordResponse.RecordCheck.builder()
                        .id(dailyRecord.getRecordId())
                        .recordDate(dailyRecord.getRecordDate())
                        .build())
                .toList();
    }

    @Transactional
    public DailyRecordResponse.Detail updateDailyRecord(DailyRecordRequest.Update request, Long dailyRecordId, Long userId) {
        DailyRecord dailyRecord = findDailyRecordById(dailyRecordId);
        validateWriter(dailyRecord, userId);

        dailyRecord.update(request);

        return DailyRecordResponse.Detail.from(dailyRecord);
    }

    public DailyRecordResponse.Detail getDailyRecordDetail(Long id, Long userId) {
        DailyRecord dailyRecord = findDailyRecordById(id);
        validateWriter(dailyRecord, userId);

        return DailyRecordResponse.Detail.from(dailyRecord);
    }

    @Transactional
    public void deleteDailyRecord(Long dailyRecordId, Long userId) {
        DailyRecord dailyRecord = findDailyRecordById(dailyRecordId);
        validateWriter(dailyRecord, userId);

        dailyRecordRepository.delete(dailyRecord);
    }

    private DailyRecord findDailyRecordById(Long dailyRecordId) {
        return dailyRecordRepository.findById(dailyRecordId)
                .orElseThrow(() -> new IllegalArgumentException("해당 기록을 찾을 수 없습니다."));
    }

    private void validateWriter(DailyRecord dailyRecord, Long userId) {
        if (!dailyRecord.getUser().getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }
    }
}
