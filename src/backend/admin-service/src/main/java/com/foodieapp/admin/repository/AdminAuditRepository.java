package com.foodieapp.admin.repository;
import com.foodieapp.admin.model.AdminAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface AdminAuditRepository extends JpaRepository<AdminAudit, Long> {
    List<AdminAudit> findByAdminIdOrderByCreatedAtDesc(Long adminId);
    List<AdminAudit> findByResourceOrderByCreatedAtDesc(String resource);
}
