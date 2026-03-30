package com.flower.repository;

import com.flower.entity.MemberRefund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRefundRepository extends JpaRepository<MemberRefund, Integer> {
    List<MemberRefund> findByOrderId(Integer orderId);
}
