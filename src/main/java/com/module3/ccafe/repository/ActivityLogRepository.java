package com.module3.ccafe.repository;

import com.module3.ccafe.entity.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityLogRepository extends JpaRepository<ActivityLog,Integer> {
    Page<ActivityLog> findByUser_UserIdOrderByCreatedAtDesc(Integer userId, Pageable pageable);
}
