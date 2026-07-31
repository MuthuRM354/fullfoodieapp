package com.foodieapp.admin.service;

import com.foodieapp.admin.model.Admin;
import com.foodieapp.admin.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository repo;

    @Transactional
    public Admin create(Admin admin) {
        if (repo.existsByEmail(admin.getEmail()))
            throw new RuntimeException("Admin already exists: " + admin.getEmail());
        return repo.save(admin);
    }

    public Admin getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Admin not found: " + id));
    }

    public List<Admin> getAll() {
        return repo.findAll();
    }

    @Transactional
    public Admin update(Long id, Admin updated) {
        Admin a = getById(id);
        if (updated.getName() != null) a.setName(updated.getName());
        if (updated.getLevel() != null) a.setLevel(updated.getLevel());
        return repo.save(a);
    }

    @Transactional
    public Admin deactivate(Long id) {
        Admin a = getById(id);
        a.setActive(false);
        return repo.save(a);
    }
}
