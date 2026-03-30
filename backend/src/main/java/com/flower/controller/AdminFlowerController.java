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
@RequestMapping("/admin/flower")
public class AdminFlowerController {

    @Autowired
    private FlowerService flowerService;

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getAllFlowers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String family,
            @RequestParam(required = false) String flowerColor,
            @RequestParam(required = false) String floweringPeriod,
            @RequestParam(required = false) Boolean isRecommended,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Map<String, Object> filters = new HashMap<>();
        if (name != null && !name.trim().isEmpty()) filters.put("name", name);
        if (family != null && !family.trim().isEmpty()) filters.put("family", family);
        if (flowerColor != null && !flowerColor.trim().isEmpty()) filters.put("flowerColor", flowerColor);
        if (floweringPeriod != null && !floweringPeriod.trim().isEmpty()) filters.put("floweringPeriod", floweringPeriod);
        if (isRecommended != null) filters.put("isRecommended", isRecommended);

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

    @GetMapping("/{id}")
    public ResponseEntity<Flower> getFlowerById(@PathVariable Integer id) {
        Flower flower = flowerService.getFlowerById(id);
        return flower != null ?
                new ResponseEntity<>(flower, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createFlower(@RequestBody Flower flower) {
        try {
            Flower created = flowerService.createFlower(flower);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "花卉创建成功");
            result.put("data", created);
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "花卉创建失败: " + e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateFlower(
            @PathVariable Integer id,
            @RequestBody Flower flower) {
        try {
            Flower updated = flowerService.updateFlower(id, flower);
            if (updated != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "花卉更新成功");
                result.put("data", updated);
                return new ResponseEntity<>(result, HttpStatus.OK);
            }
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "花卉不存在");
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "花卉更新失败: " + e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteFlower(@PathVariable Integer id) {
        try {
            flowerService.deleteFlower(id);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "花卉删除成功");
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "花卉删除失败: " + e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/batch")
    public ResponseEntity<Map<String, Object>> deleteFlowersBatch(@RequestBody List<Integer> ids) {
        try {
            int deletedCount = 0;
            for (Integer id : ids) {
                flowerService.deleteFlower(id);
                deletedCount++;
            }
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "批量删除成功，共删除 " + deletedCount + " 条记录");
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "批量删除失败: " + e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/recommend")
    public ResponseEntity<Map<String, Object>> toggleRecommend(
            @PathVariable Integer id,
            @RequestParam boolean recommended) {
        Flower flower = flowerService.getFlowerById(id);
        if (flower == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "花卉不存在");
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }
        flower.setIsRecommended(recommended);
        Flower updated = flowerService.updateFlower(id, flower);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", recommended ? "已设置推荐" : "已取消推荐");
        result.put("data", updated);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> result = new HashMap<>();
        List<String> families = flowerService.getAllFamilies();
        List<String> colors = flowerService.getAllColors();
        List<String> periods = flowerService.getAllFloweringPeriods();
        result.put("totalFamilies", families.size());
        result.put("totalColors", colors.size());
        result.put("totalFloweringPeriods", periods.size());
        result.put("families", families);
        result.put("colors", colors);
        result.put("floweringPeriods", periods);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchFlowers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Flower> flowerPage = flowerService.searchFlowers(keyword, pageable);

        Map<String, Object> result = new HashMap<>();
        result.put("content", flowerPage.getContent());
        result.put("totalElements", flowerPage.getTotalElements());
        result.put("totalPages", flowerPage.getTotalPages());
        result.put("currentPage", flowerPage.getNumber());
        result.put("pageSize", flowerPage.getSize());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}