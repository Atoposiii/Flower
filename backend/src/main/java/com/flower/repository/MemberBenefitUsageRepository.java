package com.flower.repository;

import com.flower.entity.MemberBenefitUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberBenefitUsageRepository extends JpaRepository<MemberBenefitUsage, Integer> {
    List<MemberBenefitUsage> findByUserIdOrderByUsageTimeDesc(Integer userId);
    List<MemberBenefitUsage> findByMemberRecordIdOrderByUsageTimeDesc(Integer memberRecordId);
    List<MemberBenefitUsage> findByBenefitCodeOrderByUsageTimeDesc(String benefitCode);
    boolean existsByUserIdAndMemberRecordId(Integer userId, Integer memberRecordId);
    long countByUserId(Integer userId);
}