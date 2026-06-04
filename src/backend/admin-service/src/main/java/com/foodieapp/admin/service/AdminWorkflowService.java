package com.foodieapp.admin.service;
import com.foodieapp.admin.model.AdminWorkflow;
import com.foodieapp.admin.repository.AdminWorkflowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
@Service @RequiredArgsConstructor
public class AdminWorkflowService {
    private final AdminWorkflowRepository repo;
    public AdminWorkflow create(AdminWorkflow w) { return repo.save(w); }
    public AdminWorkflow getById(Long id) { return repo.findById(id).orElseThrow(() -> new RuntimeException("Workflow not found: " + id)); }
    public List<AdminWorkflow> getAll() { return repo.findAll(); }
    public List<AdminWorkflow> getPending() { return repo.findByStatus("PENDING"); }
    public AdminWorkflow approve(Long id) {
        AdminWorkflow w = getById(id); w.setStatus("APPROVED"); return repo.save(w);
    }
    public AdminWorkflow reject(Long id) {
        AdminWorkflow w = getById(id); w.setStatus("REJECTED"); return repo.save(w);
    }
}
