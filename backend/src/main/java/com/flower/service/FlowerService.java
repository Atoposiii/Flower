package com.flower.service;

import com.flower.entity.Flower;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface FlowerService {
    Page<Flower> getFlowersByPage(Pageable pageable);
    Flower getFlowerById(Integer id);
    Flower createFlower(Flower flower);
    Flower updateFlower(Integer id, Flower flower);
    void deleteFlower(Integer id);
    Page<Flower> searchFlowers(String keyword, Pageable pageable);
    Page<Flower> getFlowersByFilters(Map<String, Object> filters, Pageable pageable);
    Page<Flower> getRecommendedFlowers(Pageable pageable);
    List<String> getAllFamilies();
    List<String> getAllColors();
    List<String> getAllFloweringPeriods();
    Map<String, Object> getFlowerDetailForMember(Integer flowerId, Integer userId);
    Map<String, Object> getFlowerDetailForNonMember(Integer flowerId);
}
