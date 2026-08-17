package com.module3.ccafe.service;

import com.module3.ccafe.dto.response.ActivityLogResponse;
import com.module3.ccafe.repository.ActivityLogRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ActivityLogService {
    final ActivityLogRepository activityLogRepository;

    public Page<ActivityLogResponse> getMyActivityLogs(Integer userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return activityLogRepository.findByUser_UserIdOrderByCreatedAtDesc(userId, pageable)
                .map(log -> ActivityLogResponse.builder()
                        .logId(log.getALogId())
                        .action(log.getAction())
                        .createdAt(log.getCreatedAt())
                        .build());
    }
}