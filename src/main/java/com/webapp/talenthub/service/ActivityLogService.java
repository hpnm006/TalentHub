package com.webapp.talenthub.service;

import com.webapp.talenthub.entity.ActivityLog;
import com.webapp.talenthub.repository.ActivityLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityLogService {

    private final ActivityLogRepository repository;

    public ActivityLogService(ActivityLogRepository repository) {
        this.repository = repository;
    }

    public void save(String username,
                     String action,
                     String description) {

        repository.save(
                new ActivityLog(
                        username,
                        action,
                        description
                )
        );
    }

    public List<ActivityLog> findAll() {
        return repository.findTop20ByOrderByCreatedAtDesc();
    }

}