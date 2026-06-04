package com.foodieapp.delivery.service;
import com.foodieapp.delivery.model.DeliveryPartner;
import com.foodieapp.delivery.repository.DeliveryPartnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
@Service @RequiredArgsConstructor
public class DeliveryPartnerService {
    private final DeliveryPartnerRepository repo;
    public DeliveryPartner create(DeliveryPartner dp) { return repo.save(dp); }
    public DeliveryPartner getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Partner not found: " + id));
    }
    public DeliveryPartner update(Long id, DeliveryPartner updated) {
        DeliveryPartner dp = getById(id);
        if (updated.getName() != null) dp.setName(updated.getName());
        if (updated.getPhone() != null) dp.setPhone(updated.getPhone());
        if (updated.getVehicleType() != null) dp.setVehicleType(updated.getVehicleType());
        if (updated.getVehicleNumber() != null) dp.setVehicleNumber(updated.getVehicleNumber());
        if (updated.getCurrentLatitude() != null) dp.setCurrentLatitude(updated.getCurrentLatitude());
        if (updated.getCurrentLongitude() != null) dp.setCurrentLongitude(updated.getCurrentLongitude());
        return repo.save(dp);
    }
    public List<DeliveryPartner> getAvailable() { return repo.findByIsAvailableTrueAndIsActiveTrue(); }
    public DeliveryPartner setAvailability(Long id, boolean available) {
        DeliveryPartner dp = getById(id);
        dp.setAvailable(available);
        return repo.save(dp);
    }
}
