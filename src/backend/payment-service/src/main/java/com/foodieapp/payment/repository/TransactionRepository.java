package com.foodieapp.payment.repository;

import com.foodieapp.payment.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByOrderId(Long orderId);
    List<Transaction> findByUserId(Long userId);
    Optional<Transaction> findByTransactionId(String transactionId);
}
