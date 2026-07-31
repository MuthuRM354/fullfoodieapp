package com.foodieapp.delivery.service;

import com.foodieapp.delivery.model.Earnings;
import com.foodieapp.delivery.repository.EarningsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EarningsService {

    private final EarningsRepository repo;

    public List<Earnings> getByPartner(Long partnerId) {
        return repo.findByDeliveryPartnerId(partnerId);
    }

    public Map<String, Object> getSummary(Long partnerId) {
        // Today window
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd   = todayStart.plusDays(1);

        // This week window (last 7 days)
        LocalDateTime weekStart  = todayStart.minusDays(6);

        BigDecimal total   = repo.sumByDeliveryPartnerId(partnerId);
        BigDecimal today   = repo.sumByDeliveryPartnerIdAndDateBetween(partnerId, todayStart, todayEnd);
        BigDecimal weekly  = repo.sumByDeliveryPartnerIdAndDateBetween(partnerId, weekStart, todayEnd);
        long deliveries    = repo.countByDeliveryPartnerId(partnerId);

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalEarnings",   total   != null ? total   : BigDecimal.ZERO);
        summary.put("todayEarnings",   today   != null ? today   : BigDecimal.ZERO);
        summary.put("weeklyEarnings",  weekly  != null ? weekly  : BigDecimal.ZERO);
        summary.put("totalDeliveries", deliveries);
        return summary;
    }

    @Transactional
    public Earnings addEarning(Earnings e) {
        return repo.save(e);
    }
}
