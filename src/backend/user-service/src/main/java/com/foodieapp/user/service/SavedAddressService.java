package com.foodieapp.user.service;

import com.foodieapp.user.dto.SavedAddressRequest;
import com.foodieapp.user.model.SavedAddress;
import com.foodieapp.user.repository.SavedAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SavedAddressService {

    private final SavedAddressRepository savedAddressRepository;

    public List<SavedAddress> list(Long userId) {
        return savedAddressRepository.findByUserIdOrderByIdDesc(userId);
    }

    @Transactional
    public SavedAddress create(Long userId, SavedAddressRequest request) {
        if (request.isDefault()) {
            clearExistingDefault(userId);
        }
        // First saved address is the default automatically, regardless of the request.
        boolean makeDefault = request.isDefault() || savedAddressRepository.findByUserIdOrderByIdDesc(userId).isEmpty();
        SavedAddress address = SavedAddress.builder()
                .userId(userId)
                .label(request.getLabel())
                .addressLine(request.getAddressLine())
                .isDefault(makeDefault)
                .build();
        return savedAddressRepository.save(address);
    }

    @Transactional
    public SavedAddress update(Long userId, Long addressId, SavedAddressRequest request) {
        SavedAddress address = savedAddressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        address.setLabel(request.getLabel());
        address.setAddressLine(request.getAddressLine());
        if (request.isDefault() && !address.isDefault()) {
            clearExistingDefault(userId);
            address.setDefault(true);
        }
        return savedAddressRepository.save(address);
    }

    @Transactional
    public void delete(Long userId, Long addressId) {
        SavedAddress address = savedAddressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        boolean wasDefault = address.isDefault();
        savedAddressRepository.delete(address);

        // Promote the most recently added remaining address to default so the
        // user always has one, instead of silently ending up with none.
        if (wasDefault) {
            savedAddressRepository.findByUserIdOrderByIdDesc(userId).stream()
                    .findFirst()
                    .ifPresent(next -> {
                        next.setDefault(true);
                        savedAddressRepository.save(next);
                    });
        }
    }

    private void clearExistingDefault(Long userId) {
        savedAddressRepository.findByUserIdOrderByIdDesc(userId).forEach(a -> {
            if (a.isDefault()) {
                a.setDefault(false);
                savedAddressRepository.save(a);
            }
        });
    }
}
