package com.gm.goalmate.controller;

import com.gm.goalmate.config.LoginUser;
import com.gm.goalmate.domain.user.User;
import com.gm.goalmate.dto.DailyRecordRequest;
import com.gm.goalmate.dto.DailyRecordResponse;
import com.gm.goalmate.service.DailyRecordService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/daily-records")
public class DailyRecordController {

    private final DailyRecordService dailyRecordService;

    public DailyRecordController(DailyRecordService dailyRecordService) {
        this.dailyRecordService = dailyRecordService;
    }

    @PostMapping
    public ResponseEntity<Void> addDailyRecord(@Valid @RequestBody DailyRecordRequest.Add request, @LoginUser User user) {
        Long id = dailyRecordService.addDailyRecord(request, user.getUserId());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping
    public ResponseEntity<List<DailyRecordResponse.RecordCheck>> getDailyRecordList(@RequestParam(value = "year") int year, @LoginUser User user) {
        List<DailyRecordResponse.RecordCheck> recordChecks = dailyRecordService.getRecordChecksByYear(user.getUserId(), year);
        return ResponseEntity.ok(recordChecks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DailyRecordResponse.Detail> getDailyRecordDetail(@PathVariable Long id, @LoginUser User user) {
        DailyRecordResponse.Detail dailyRecord = dailyRecordService.getDailyRecordDetail(id, user.getUserId());
        return ResponseEntity.ok(dailyRecord);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DailyRecordResponse.Detail> updateDailyRecord(@Valid @RequestBody DailyRecordRequest.Update request, @LoginUser User user, @PathVariable Long id) {
        DailyRecordResponse.Detail dailyRecord = dailyRecordService.updateDailyRecord(request, id, user.getUserId());
        return ResponseEntity.ok(dailyRecord);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDailyRecord(@LoginUser User user, @PathVariable Long id) {
        dailyRecordService.deleteDailyRecord(id, user.getUserId());
        return ResponseEntity.noContent().build();
    }
}
