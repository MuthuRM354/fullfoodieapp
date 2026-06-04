package com.foodieapp.admin.repository;
import com.foodieapp.admin.model.AdminActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface AdminActivityRepository extends JpaRepository<AdminActivity, Long> {
    List<AdminActivity> findByAdminIdOrderByCreatedAtDesc(Long adminId);
}
