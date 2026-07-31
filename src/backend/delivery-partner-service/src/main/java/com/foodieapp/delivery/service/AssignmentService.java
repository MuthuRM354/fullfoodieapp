package com.foodieapp.delivery.service;

import com.foodieapp.delivery.model.Assignment;
import com.foodieapp.delivery.model.AssignmentStatus;
import com.foodieapp.delivery.repository.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository repo;

    @Transactional
    public Assignment create(Assignment a) {
        return repo.save(a);
    }

    public Assignment getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Assignment not found: " + id));
    }

    @Transactional
    public Assignment updateStatus(Long id, AssignmentStatus status) {
        Assignment a = getById(id);
        a.setStatus(status);
        if (status == AssignmentStatus.PICKED_UP) a.setPickedAt(LocalDateTime.now());
        if (status == AssignmentStatus.DELIVERED) a.setDeliveredAt(LocalDateTime.now());
        return repo.save(a);
    }

    public List<Assignment> getByPartner(Long partnerId) {
        return repo.findByDeliveryPartnerId(partnerId);
    }
}
