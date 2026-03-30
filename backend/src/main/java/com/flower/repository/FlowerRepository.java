package com.flower.repository;

import com.flower.entity.Flower;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlowerRepository extends JpaRepository<Flower, Integer>, JpaSpecificationExecutor<Flower> {

    Page<Flower> findAll(Pageable pageable);

    Page<Flower> findByIsRecommended(Boolean isRecommended, Pageable pageable);

    Page<Flower> findByFamily(String family, Pageable pageable);

    Page<Flower> findByFloweringPeriodContaining(String floweringPeriod, Pageable pageable);

    Page<Flower> findByNameContaining(String name, Pageable pageable);

    @Query("SELECT f FROM Flower f WHERE " +
           "(:name IS NULL OR f.name LIKE %:name%) AND " +
           "(:family IS NULL OR f.family = :family) AND " +
           "(:flowerColor IS NULL OR f.flowerColor LIKE %:flowerColor%) AND " +
           "(:floweringPeriod IS NULL OR f.floweringPeriod LIKE %:floweringPeriod%) AND " +
           "(:isRecommended IS NULL OR f.isRecommended = :isRecommended) AND " +
           "(:difficultyLevel IS NULL OR f.difficultyLevel = :difficultyLevel) AND " +
           "(:suitableSeason IS NULL OR f.suitableSeason = :suitableSeason)")
    Page<Flower> findByFilters(
            @Param("name") String name,
            @Param("family") String family,
            @Param("flowerColor") String flowerColor,
            @Param("floweringPeriod") String floweringPeriod,
            @Param("isRecommended") Boolean isRecommended,
            @Param("difficultyLevel") String difficultyLevel,
            @Param("suitableSeason") String suitableSeason,
            Pageable pageable);

    @Query("SELECT DISTINCT f.family FROM Flower f WHERE f.family IS NOT NULL ORDER BY f.family")
    List<String> findAllFamilies();

    @Query("SELECT DISTINCT f.flowerColor FROM Flower f WHERE f.flowerColor IS NOT NULL")
    List<String> findAllColors();

    @Query("SELECT DISTINCT f.floweringPeriod FROM Flower f WHERE f.floweringPeriod IS NOT NULL ORDER BY f.floweringPeriod")
    List<String> findAllFloweringPeriods();
}
