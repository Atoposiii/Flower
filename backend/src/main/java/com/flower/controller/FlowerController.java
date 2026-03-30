package com.flower.controller;

import com.flower.entity.Flower;
import com.flower.service.FlowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/flower")
public class FlowerController {

    @Autowired
    private FlowerService flowerService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getFlowers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String family,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String floweringPeriod,
            @RequestParam(required = false) Boolean isRecommended,
            @RequestParam(required = false) String difficultyLevel,
            @RequestParam(required = false) String suitableSeason,
            @RequestParam(defaultValue = "createTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Map<String, Object> filters = new HashMap<>();
        if (name != null && !name.trim().isEmpty()) filters.put("name", name);
        if (family != null && !family.trim().isEmpty()) filters.put("family", family);
        if (color != null && !color.trim().isEmpty()) filters.put("flowerColor", color);
        if (floweringPeriod != null && !floweringPeriod.trim().isEmpty()) filters.put("floweringPeriod", floweringPeriod);
        if (isRecommended != null) filters.put("isRecommended", isRecommended);
        if (difficultyLevel != null && !difficultyLevel.trim().isEmpty()) filters.put("difficultyLevel", difficultyLevel);
        if (suitableSeason != null && !suitableSeason.trim().isEmpty()) filters.put("suitableSeason", suitableSeason);

        Page<Flower> flowerPage;
        if (filters.isEmpty()) {
            flowerPage = flowerService.getFlowersByPage(pageable);
        } else {
            flowerPage = flowerService.getFlowersByFilters(filters, pageable);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("content", flowerPage.getContent());
        result.put("totalElements", flowerPage.getTotalElements());
        result.put("totalPages", flowerPage.getTotalPages());
        result.put("currentPage", flowerPage.getNumber());
        result.put("pageSize", flowerPage.getSize());
        result.put("hasNext", flowerPage.hasNext());
        result.put("hasPrevious", flowerPage.hasPrevious());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<Map<String, Object>> getFlowerDetailForNonMember(@PathVariable Integer id) {
        Map<String, Object> result = flowerService.getFlowerDetailForNonMember(id);
        return result != null ?
                new ResponseEntity<>(result, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/detail/{id}/member/{userId}")
    public ResponseEntity<Map<String, Object>> getFlowerDetailForMember(
            @PathVariable Integer id,
            @PathVariable Integer userId) {
        Map<String, Object> result = flowerService.getFlowerDetailForMember(id, userId);
        return result != null ?
                new ResponseEntity<>(result, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/recommended")
    public ResponseEntity<Map<String, Object>> getRecommendedFlowers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Flower> flowerPage = flowerService.getRecommendedFlowers(pageable);

        Map<String, Object> result = new HashMap<>();
        result.put("content", flowerPage.getContent());
        result.put("totalElements", flowerPage.getTotalElements());
        result.put("totalPages", flowerPage.getTotalPages());
        result.put("currentPage", flowerPage.getNumber());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchFlowers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Flower> flowerPage = flowerService.searchFlowers(keyword, pageable);

        Map<String, Object> result = new HashMap<>();
        result.put("content", flowerPage.getContent());
        result.put("totalElements", flowerPage.getTotalElements());
        result.put("totalPages", flowerPage.getTotalPages());
        result.put("currentPage", flowerPage.getNumber());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/filters/options")
    public ResponseEntity<Map<String, List<String>>> getFilterOptions() {
        Map<String, List<String>> options = new HashMap<>();
        options.put("families", flowerService.getAllFamilies());
        options.put("colors", flowerService.getAllColors());
        options.put("floweringPeriods", flowerService.getAllFloweringPeriods());
        return new ResponseEntity<>(options, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Flower> createFlower(@RequestBody Flower flower) {
        Flower created = flowerService.createFlower(flower);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Flower> updateFlower(@PathVariable Integer id, @RequestBody Flower flower) {
        Flower updated = flowerService.updateFlower(id, flower);
        return updated != null ?
                new ResponseEntity<>(updated, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlower(@PathVariable Integer id) {
        flowerService.deleteFlower(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
