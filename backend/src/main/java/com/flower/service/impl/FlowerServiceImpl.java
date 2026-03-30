package com.flower.service.impl;

import com.flower.entity.Flower;
import com.flower.entity.MemberRecord;
import com.flower.entity.User;
import com.flower.repository.FlowerRepository;
import com.flower.repository.MemberRecordRepository;
import com.flower.repository.UserRepository;
import com.flower.service.FlowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FlowerServiceImpl implements FlowerService {

    @Autowired
    private FlowerRepository flowerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MemberRecordRepository memberRecordRepository;

    @Override
    public Page<Flower> getFlowersByPage(Pageable pageable) {
        return flowerRepository.findAll(pageable);
    }

    @Override
    public Flower getFlowerById(Integer id) {
        if (id == null) {
            return null;
        }
        Optional<Flower> flower = flowerRepository.findById(id);
        return flower.orElse(null);
    }

    @Override
    public Flower createFlower(Flower flower) {
        if (flower == null) {
            return null;
        }
        return flowerRepository.save(flower);
    }

    @Override
    public Flower updateFlower(Integer id, Flower flower) {
        if (id == null || flower == null) {
            return null;
        }
        if (flowerRepository.existsById(id)) {
            flower.setId(id);
            return flowerRepository.save(flower);
        }
        return null;
    }

    @Override
    public void deleteFlower(Integer id) {
        if (id != null) {
            flowerRepository.deleteById(id);
        }
    }

    @Override
    public Page<Flower> searchFlowers(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return flowerRepository.findAll(pageable);
        }
        return flowerRepository.findByNameContaining(keyword, pageable);
    }

    @Override
    public Page<Flower> getFlowersByFilters(Map<String, Object> filters, Pageable pageable) {
        String name = getStringValue(filters, "name");
        String family = getStringValue(filters, "family");
        String flowerColor = getStringValue(filters, "flowerColor");
        String floweringPeriod = getStringValue(filters, "floweringPeriod");
        Boolean isRecommended = getBooleanValue(filters, "isRecommended");
        String difficultyLevel = getStringValue(filters, "difficultyLevel");
        String suitableSeason = getStringValue(filters, "suitableSeason");

        return flowerRepository.findByFilters(
                name, family, flowerColor, floweringPeriod,
                isRecommended, difficultyLevel, suitableSeason,
                pageable);
    }

    @Override
    public Page<Flower> getRecommendedFlowers(Pageable pageable) {
        return flowerRepository.findByIsRecommended(true, pageable);
    }

    @Override
    public List<String> getAllFamilies() {
        return flowerRepository.findAllFamilies();
    }

    @Override
    public List<String> getAllColors() {
        return flowerRepository.findAllColors();
    }

    @Override
    public List<String> getAllFloweringPeriods() {
        return flowerRepository.findAllFloweringPeriods();
    }

    @Override
    public Map<String, Object> getFlowerDetailForMember(Integer flowerId, Integer userId) {
        Flower flower = getFlowerById(flowerId);
        if (flower == null) {
            return null;
        }

        boolean isMember = checkUserIsMember(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("flower", flower);
        result.put("isMember", isMember);
        result.put("isFullDetail", true);

        if (isMember) {
            Map<String, Object> fullDetail = buildFullDetail(flower);
            result.put("fullDetail", fullDetail);
        }

        return result;
    }

    @Override
    public Map<String, Object> getFlowerDetailForNonMember(Integer flowerId) {
        Flower flower = getFlowerById(flowerId);
        if (flower == null) {
            return null;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("flower", flower);
        result.put("isMember", false);
        result.put("isFullDetail", false);

        Map<String, Object> basicDetail = buildBasicDetail(flower);
        result.put("basicDetail", basicDetail);

        return result;
    }

    private boolean checkUserIsMember(Integer userId) {
        if (userId == null) {
            return false;
        }
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            return false;
        }
        Optional<MemberRecord> memberRecord = memberRecordRepository.findByUserId(userId);
        return memberRecord.isPresent() && "active".equals(memberRecord.get().getStatus());
    }

    private Map<String, Object> buildBasicDetail(Flower flower) {
        Map<String, Object> detail = new HashMap<>();
        detail.put("name", flower.getName());
        detail.put("aliases", flower.getAliases());
        detail.put("scientificName", flower.getScientificName());
        detail.put("family", flower.getFamily());
        detail.put("genus", flower.getGenus());
        detail.put("category", flower.getCategory());
        detail.put("plantType", flower.getPlantType());
        detail.put("lifeCycle", flower.getLifeCycle());
        detail.put("ornamentalType", flower.getOrnamentalType());
        detail.put("origin", flower.getOrigin());
        detail.put("distribution", flower.getDistribution());
        detail.put("description", flower.getDescription());
        detail.put("plantHeight", flower.getPlantHeight());
        detail.put("crownWidth", flower.getCrownWidth());
        detail.put("leafShape", flower.getLeafShape());
        detail.put("leafColor", flower.getLeafColor());
        detail.put("floweringPeriod", flower.getFloweringPeriod());
        detail.put("flowerType", flower.getFlowerType());
        detail.put("flowerColor", flower.getFlowerColor());
        detail.put("flowerFragrance", flower.getFlowerFragrance());
        detail.put("fruitPeriod", flower.getFruitPeriod());
        detail.put("fruitType", flower.getFruitType());
        detail.put("flowerLanguage", flower.getFlowerLanguage());
        detail.put("culturalMeaning", flower.getCulturalMeaning());
        detail.put("isToxic", flower.getIsToxic());
        detail.put("toxicToHuman", flower.getToxicToHuman());
        detail.put("toxicToPet", flower.getToxicToPet());
        detail.put("isEdible", flower.getIsEdible());
        detail.put("isCityFlower", flower.getIsCityFlower());
        detail.put("cityFlowerOf", flower.getCityFlowerOf());
        detail.put("imageUrl", flower.getImageUrl());
        detail.put("difficultyLevel", flower.getDifficultyLevel());
        detail.put("suitableSeason", flower.getSuitableSeason());
        return detail;
    }

    private Map<String, Object> buildFullDetail(Flower flower) {
        Map<String, Object> detail = new HashMap<>();

        detail.put("lightRequirements", flower.getLightRequirements());
        detail.put("temperatureRequirements", flower.getTemperatureRequirements());
        detail.put("coldTolerance", flower.getColdTolerance());
        detail.put("heatTolerance", flower.getHeatTolerance());
        detail.put("suitableTemperature", flower.getSuitableTemperature());
        detail.put("waterRequirements", flower.getWaterRequirements());
        detail.put("soilType", flower.getSoilType());
        detail.put("soilPh", flower.getSoilPh());
        detail.put("soilRequirements", flower.getSoilRequirements());
        detail.put("growthCycle", flower.getGrowthCycle());
        detail.put("dormancyPeriod", flower.getDormancyPeriod());
        detail.put("growthSpeed", flower.getGrowthSpeed());

        detail.put("wateringMethod", flower.getWateringMethod());
        detail.put("wateringFrequency", flower.getWateringFrequency());
        detail.put("fertilizerType", flower.getFertilizerType());
        detail.put("fertilizerPeriod", flower.getFertilizerPeriod());
        detail.put("fertilizerAmount", flower.getFertilizerAmount());
        detail.put("pruningTime", flower.getPruningTime());
        detail.put("pruningMethod", flower.getPruningMethod());
        detail.put("propagationMethods", flower.getPropagationMethods());
        detail.put("repottingTips", flower.getRepottingTips());

        detail.put("commonPests", flower.getCommonPests());
        detail.put("commonDiseases", flower.getCommonDiseases());
        detail.put("pestControl", flower.getPestControl());

        detail.put("ornamentalUsage", flower.getOrnamentalUsage());
        detail.put("landscapeUsage", flower.getLandscapeUsage());
        detail.put("cutFlowerUsage", flower.getCutFlowerUsage());
        detail.put("medicinalValue", flower.getMedicinalValue());
        detail.put("edibleValue", flower.getEdibleValue());
        detail.put("fragranceValue", flower.getFragranceValue());
        detail.put("airPurification", flower.getAirPurification());
        detail.put("plantCombinations", flower.getPlantCombinations());

        detail.put("purchaseTips", flower.getPurchaseTips());
        detail.put("beginnerFriendly", flower.getBeginnerFriendly());
        detail.put("placementSuggestion", flower.getPlacementSuggestion());
        detail.put("commonProblems", flower.getCommonProblems());
        detail.put("yellowLeafCauses", flower.getYellowLeafCauses());
        detail.put("noFloweringCauses", flower.getNoFloweringCauses());
        detail.put("rootRotCauses", flower.getRootRotCauses());
        detail.put("winterCare", flower.getWinterCare());
        detail.put("summerCare", flower.getSummerCare());

        detail.put("lifespan", flower.getLifespan());
        detail.put("growthRate", flower.getGrowthRate());
        detail.put("festivalUsage", flower.getFestivalUsage());
        detail.put("occasionUsage", flower.getOccasionUsage());

        return detail;
    }

    private String getStringValue(Map<String, Object> filters, String key) {
        Object value = filters.get(key);
        if (value == null || value.toString().trim().isEmpty()) {
            return null;
        }
        return value.toString();
    }

    private Boolean getBooleanValue(Map<String, Object> filters, String key) {
        Object value = filters.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        String strValue = value.toString().toLowerCase();
        if ("true".equals(strValue) || "1".equals(strValue)) {
            return true;
        } else if ("false".equals(strValue) || "0".equals(strValue)) {
            return false;
        }
        return null;
    }
}
