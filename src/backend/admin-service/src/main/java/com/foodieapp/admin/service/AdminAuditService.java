package com.foodieapp.admin.service;

import com.foodieapp.admin.model.AdminAudit;
import com.foodieapp.admin.repository.AdminAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminAuditService {

    private final AdminAuditRepository repo;

    @Transactional
    public AdminAudit log(Long adminId, String action, String resource, Long resourceId, String details) {
        return repo.save(AdminAudit.builder()
                .adminId(adminId).action(action).resource(resource)
                .resourceId(resourceId).details(details).build());
    }

    public List<AdminAudit> getAll() {
        return repo.findAll();
    }

    public List<AdminAudit> getByAdmin(Long adminId) {
        return repo.findByAdminIdOrderByCreatedAtDesc(adminId);
    }
}
