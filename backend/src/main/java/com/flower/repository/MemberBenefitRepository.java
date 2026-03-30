package com.flower.repository;

import com.flower.entity.MemberBenefit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberBenefitRepository extends JpaRepository<MemberBenefit, Integer> {
    List<MemberBenefit> findByIsActiveOrderBySortOrderAsc(Boolean isActive);

    List<MemberBenefit> findAllByOrderBySortOrderAsc();
}