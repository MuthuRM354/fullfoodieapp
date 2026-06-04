package com.foodieapp.delivery.service;
import com.foodieapp.delivery.model.Earnings;
import com.foodieapp.delivery.repository.EarningsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
@Service @RequiredArgsConstructor
public class EarningsService {
    private final EarningsRepository repo;
    public List<Earnings> getByPartner(Long partnerId) { return repo.findByDeliveryPartnerId(partnerId); }
    public Map<String, Object> getSummary(Long partnerId) {
        List<Earnings> earnings = repo.findByDeliveryPartnerId(partnerId);
        BigDecimal total = repo.sumByDeliveryPartnerId(partnerId);
        if (total == null) total = BigDecimal.ZERO;
        return Map.of("partnerId", partnerId, "totalEarnings", total, "transactions", earnings.size());
    }
    public Earnings addEarning(Earnings e) { return repo.save(e); }
}
