package com.flower.repository;

import com.flower.entity.PaymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRecordRepository extends JpaRepository<PaymentRecord, Integer> {
    List<PaymentRecord> findByUserIdOrderByCreateTimeDesc(Integer userId);

    Optional<PaymentRecord> findByOrderNumber(String orderNumber);

    List<PaymentRecord> findByPaymentStatus(String paymentStatus);

    Optional<PaymentRecord> findByTransactionId(String transactionId);
}