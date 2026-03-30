package com.flower.repository;

import com.flower.entity.VolunteerActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VolunteerActivityRepository extends JpaRepository<VolunteerActivity, Integer> {
    // 按状态查询活动
    List<VolunteerActivity> findByStatus(String status);

    // 按状态列表查询活动
    List<VolunteerActivity> findByStatusIn(List<String> statuses);

    // 按活动时间排序查询
    List<VolunteerActivity> findByOrderByActivityTimeDesc();
}