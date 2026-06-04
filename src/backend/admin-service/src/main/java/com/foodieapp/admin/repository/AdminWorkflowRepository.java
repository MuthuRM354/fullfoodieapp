package com.foodieapp.admin.repository;
import com.foodieapp.admin.model.AdminWorkflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface AdminWorkflowRepository extends JpaRepository<AdminWorkflow, Long> {
    List<AdminWorkflow> findByStatus(String status);
    List<AdminWorkflow> findByRequestedBy(Long requestedBy);
}
