package com.flower.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "flower")
public class Flower {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "aliases", length = 500)
    private String aliases;

    @Column(name = "scientific_name", length = 200)
    private String scientificName;

    @Column(name = "family", length = 100)
    private String family;

    @Column(name = "genus", length = 100)
    private String genus;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "plant_type", length = 100)
    private String plantType;

    @Column(name = "life_cycle", length = 50)
    private String lifeCycle;

    @Column(name = "ornamental_type", length = 100)
    private String ornamentalType;

    @Column(name = "origin", length = 500)
    private String origin;

    @Column(name = "distribution", length = 500)
    private String distribution;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "plant_height", length = 100)
    private String plantHeight;

    @Column(name = "crown_width", length = 100)
    private String crownWidth;

    @Column(name = "leaf_shape", length = 100)
    private String leafShape;

    @Column(name = "leaf_color", length = 200)
    private String leafColor;

    @Column(name = "flowering_period", length = 100)
    private String floweringPeriod;

    @Column(name = "flower_type", length = 100)
    private String flowerType;

    @Column(name = "flower_color", length = 200)
    private String flowerColor;

    @Column(name = "flower_fragrance", length = 100)
    private String flowerFragrance;

    @Column(name = "fruit_period", length = 100)
    private String fruitPeriod;

    @Column(name = "fruit_type", length = 100)
    private String fruitType;

    @Column(name = "flower_language", columnDefinition = "TEXT")
    private String flowerLanguage;

    @Column(name = "cultural_meaning", columnDefinition = "TEXT")
    private String culturalMeaning;

    @Column(name = "light_requirements", length = 100)
    private String lightRequirements;

    @Column(name = "temperature_requirements", length = 200)
    private String temperatureRequirements;

    @Column(name = "cold_tolerance", length = 100)
    private String coldTolerance;

    @Column(name = "heat_tolerance", length = 100)
    private String heatTolerance;

    @Column(name = "suitable_temperature", length = 200)
    private String suitableTemperature;

    @Column(name = "water_requirements", length = 100)
    private String waterRequirements;

    @Column(name = "soil_type", length = 200)
    private String soilType;

    @Column(name = "soil_ph", length = 100)
    private String soilPh;

    @Column(name = "soil_requirements", columnDefinition = "TEXT")
    private String soilRequirements;

    @Column(name = "growth_cycle", length = 200)
    private String growthCycle;

    @Column(name = "dormancy_period", length = 200)
    private String dormancyPeriod;

    @Column(name = "growth_speed", length = 50)
    private String growthSpeed;

    @Column(name = "watering_method", columnDefinition = "TEXT")
    private String wateringMethod;

    @Column(name = "watering_frequency", length = 200)
    private String wateringFrequency;

    @Column(name = "fertilizer_type", columnDefinition = "TEXT")
    private String fertilizerType;

    @Column(name = "fertilizer_period", columnDefinition = "TEXT")
    private String fertilizerPeriod;

    @Column(name = "fertilizer_amount", columnDefinition = "TEXT")
    private String fertilizerAmount;

    @Column(name = "pruning_time", columnDefinition = "TEXT")
    private String pruningTime;

    @Column(name = "pruning_method", columnDefinition = "TEXT")
    private String pruningMethod;

    @Column(name = "propagation_methods", columnDefinition = "TEXT")
    private String propagationMethods;

    @Column(name = "repotting_tips", columnDefinition = "TEXT")
    private String repottingTips;

    @Column(name = "common_pests", columnDefinition = "TEXT")
    private String commonPests;

    @Column(name = "common_diseases", columnDefinition = "TEXT")
    private String commonDiseases;

    @Column(name = "pest_control", columnDefinition = "TEXT")
    private String pestControl;

    @Column(name = "ornamental_usage", columnDefinition = "TEXT")
    private String ornamentalUsage;

    @Column(name = "landscape_usage", columnDefinition = "TEXT")
    private String landscapeUsage;

    @Column(name = "cut_flower_usage", columnDefinition = "TEXT")
    private String cutFlowerUsage;

    @Column(name = "medicinal_value", columnDefinition = "TEXT")
    private String medicinalValue;

    @Column(name = "edible_value", columnDefinition = "TEXT")
    private String edibleValue;

    @Column(name = "fragrance_value", columnDefinition = "TEXT")
    private String fragranceValue;

    @Column(name = "air_purification", columnDefinition = "TEXT")
    private String airPurification;

    @Column(name = "plant_combinations", columnDefinition = "TEXT")
    private String plantCombinations;

    @Column(name = "purchase_tips", columnDefinition = "TEXT")
    private String purchaseTips;

    @Column(name = "beginner_friendly", length = 50)
    private String beginnerFriendly;

    @Column(name = "placement_suggestion", columnDefinition = "TEXT")
    private String placementSuggestion;

    @Column(name = "common_problems", columnDefinition = "TEXT")
    private String commonProblems;

    @Column(name = "yellow_leaf_causes", columnDefinition = "TEXT")
    private String yellowLeafCauses;

    @Column(name = "no_flowering_causes", columnDefinition = "TEXT")
    private String noFloweringCauses;

    @Column(name = "root_rot_causes", columnDefinition = "TEXT")
    private String rootRotCauses;

    @Column(name = "winter_care", columnDefinition = "TEXT")
    private String winterCare;

    @Column(name = "summer_care", columnDefinition = "TEXT")
    private String summerCare;

    @Column(name = "lifespan", length = 100)
    private String lifespan;

    @Column(name = "growth_rate", length = 100)
    private String growthRate;

    @Column(name = "is_toxic", length = 100)
    private String isToxic;

    @Column(name = "toxic_to_human", length = 100)
    private String toxicToHuman;

    @Column(name = "toxic_to_pet", length = 100)
    private String toxicToPet;

    @Column(name = "is_edible", length = 100)
    private String isEdible;

    @Column(name = "is_city_flower", length = 50)
    private String isCityFlower;

    @Column(name = "city_flower_of", length = 200)
    private String cityFlowerOf;

    @Column(name = "festival_usage", columnDefinition = "TEXT")
    private String festivalUsage;

    @Column(name = "occasion_usage", columnDefinition = "TEXT")
    private String occasionUsage;

    @Column(name = "suitable_season", length = 100)
    private String suitableSeason;

    @Column(name = "difficulty_level", length = 50)
    private String difficultyLevel;

    @Column(name = "is_recommended")
    private Boolean isRecommended;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = new Date();
        updateTime = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = new Date();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAliases() {
        return aliases;
    }

    public void setAliases(String aliases) {
        this.aliases = aliases;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getGenus() {
        return genus;
    }

    public void setGenus(String genus) {
        this.genus = genus;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPlantType() {
        return plantType;
    }

    public void setPlantType(String plantType) {
        this.plantType = plantType;
    }

    public String getLifeCycle() {
        return lifeCycle;
    }

    public void setLifeCycle(String lifeCycle) {
        this.lifeCycle = lifeCycle;
    }

    public String getOrnamentalType() {
        return ornamentalType;
    }

    public void setOrnamentalType(String ornamentalType) {
        this.ornamentalType = ornamentalType;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDistribution() {
        return distribution;
    }

    public void setDistribution(String distribution) {
        this.distribution = distribution;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlantHeight() {
        return plantHeight;
    }

    public void setPlantHeight(String plantHeight) {
        this.plantHeight = plantHeight;
    }

    public String getCrownWidth() {
        return crownWidth;
    }

    public void setCrownWidth(String crownWidth) {
        this.crownWidth = crownWidth;
    }

    public String getLeafShape() {
        return leafShape;
    }

    public void setLeafShape(String leafShape) {
        this.leafShape = leafShape;
    }

    public String getLeafColor() {
        return leafColor;
    }

    public void setLeafColor(String leafColor) {
        this.leafColor = leafColor;
    }

    public String getFloweringPeriod() {
        return floweringPeriod;
    }

    public void setFloweringPeriod(String floweringPeriod) {
        this.floweringPeriod = floweringPeriod;
    }

    public String getFlowerType() {
        return flowerType;
    }

    public void setFlowerType(String flowerType) {
        this.flowerType = flowerType;
    }

    public String getFlowerColor() {
        return flowerColor;
    }

    public void setFlowerColor(String flowerColor) {
        this.flowerColor = flowerColor;
    }

    public String getFlowerFragrance() {
        return flowerFragrance;
    }

    public void setFlowerFragrance(String flowerFragrance) {
        this.flowerFragrance = flowerFragrance;
    }

    public String getFruitPeriod() {
        return fruitPeriod;
    }

    public void setFruitPeriod(String fruitPeriod) {
        this.fruitPeriod = fruitPeriod;
    }

    public String getFruitType() {
        return fruitType;
    }

    public void setFruitType(String fruitType) {
        this.fruitType = fruitType;
    }

    public String getFlowerLanguage() {
        return flowerLanguage;
    }

    public void setFlowerLanguage(String flowerLanguage) {
        this.flowerLanguage = flowerLanguage;
    }

    public String getCulturalMeaning() {
        return culturalMeaning;
    }

    public void setCulturalMeaning(String culturalMeaning) {
        this.culturalMeaning = culturalMeaning;
    }

    public String getLightRequirements() {
        return lightRequirements;
    }

    public void setLightRequirements(String lightRequirements) {
        this.lightRequirements = lightRequirements;
    }

    public String getTemperatureRequirements() {
        return temperatureRequirements;
    }

    public void setTemperatureRequirements(String temperatureRequirements) {
        this.temperatureRequirements = temperatureRequirements;
    }

    public String getColdTolerance() {
        return coldTolerance;
    }

    public void setColdTolerance(String coldTolerance) {
        this.coldTolerance = coldTolerance;
    }

    public String getHeatTolerance() {
        return heatTolerance;
    }

    public void setHeatTolerance(String heatTolerance) {
        this.heatTolerance = heatTolerance;
    }

    public String getSuitableTemperature() {
        return suitableTemperature;
    }

    public void setSuitableTemperature(String suitableTemperature) {
        this.suitableTemperature = suitableTemperature;
    }

    public String getWaterRequirements() {
        return waterRequirements;
    }

    public void setWaterRequirements(String waterRequirements) {
        this.waterRequirements = waterRequirements;
    }

    public String getSoilType() {
        return soilType;
    }

    public void setSoilType(String soilType) {
        this.soilType = soilType;
    }

    public String getSoilPh() {
        return soilPh;
    }

    public void setSoilPh(String soilPh) {
        this.soilPh = soilPh;
    }

    public String getSoilRequirements() {
        return soilRequirements;
    }

    public void setSoilRequirements(String soilRequirements) {
        this.soilRequirements = soilRequirements;
    }

    public String getGrowthCycle() {
        return growthCycle;
    }

    public void setGrowthCycle(String growthCycle) {
        this.growthCycle = growthCycle;
    }

    public String getDormancyPeriod() {
        return dormancyPeriod;
    }

    public void setDormancyPeriod(String dormancyPeriod) {
        this.dormancyPeriod = dormancyPeriod;
    }

    public String getGrowthSpeed() {
        return growthSpeed;
    }

    public void setGrowthSpeed(String growthSpeed) {
        this.growthSpeed = growthSpeed;
    }

    public String getWateringMethod() {
        return wateringMethod;
    }

    public void setWateringMethod(String wateringMethod) {
        this.wateringMethod = wateringMethod;
    }

    public String getWateringFrequency() {
        return wateringFrequency;
    }

    public void setWateringFrequency(String wateringFrequency) {
        this.wateringFrequency = wateringFrequency;
    }

    public String getFertilizerType() {
        return fertilizerType;
    }

    public void setFertilizerType(String fertilizerType) {
        this.fertilizerType = fertilizerType;
    }

    public String getFertilizerPeriod() {
        return fertilizerPeriod;
    }

    public void setFertilizerPeriod(String fertilizerPeriod) {
        this.fertilizerPeriod = fertilizerPeriod;
    }

    public String getFertilizerAmount() {
        return fertilizerAmount;
    }

    public void setFertilizerAmount(String fertilizerAmount) {
        this.fertilizerAmount = fertilizerAmount;
    }

    public String getPruningTime() {
        return pruningTime;
    }

    public void setPruningTime(String pruningTime) {
        this.pruningTime = pruningTime;
    }

    public String getPruningMethod() {
        return pruningMethod;
    }

    public void setPruningMethod(String pruningMethod) {
        this.pruningMethod = pruningMethod;
    }

    public String getPropagationMethods() {
        return propagationMethods;
    }

    public void setPropagationMethods(String propagationMethods) {
        this.propagationMethods = propagationMethods;
    }

    public String getRepottingTips() {
        return repottingTips;
    }

    public void setRepottingTips(String repottingTips) {
        this.repottingTips = repottingTips;
    }

    public String getCommonPests() {
        return commonPests;
    }

    public void setCommonPests(String commonPests) {
        this.commonPests = commonPests;
    }

    public String getCommonDiseases() {
        return commonDiseases;
    }

    public void setCommonDiseases(String commonDiseases) {
        this.commonDiseases = commonDiseases;
    }

    public String getPestControl() {
        return pestControl;
    }

    public void setPestControl(String pestControl) {
        this.pestControl = pestControl;
    }

    public String getOrnamentalUsage() {
        return ornamentalUsage;
    }

    public void setOrnamentalUsage(String ornamentalUsage) {
        this.ornamentalUsage = ornamentalUsage;
    }

    public String getLandscapeUsage() {
        return landscapeUsage;
    }

    public void setLandscapeUsage(String landscapeUsage) {
        this.landscapeUsage = landscapeUsage;
    }

    public String getCutFlowerUsage() {
        return cutFlowerUsage;
    }

    public void setCutFlowerUsage(String cutFlowerUsage) {
        this.cutFlowerUsage = cutFlowerUsage;
    }

    public String getMedicinalValue() {
        return medicinalValue;
    }

    public void setMedicinalValue(String medicinalValue) {
        this.medicinalValue = medicinalValue;
    }

    public String getEdibleValue() {
        return edibleValue;
    }

    public void setEdibleValue(String edibleValue) {
        this.edibleValue = edibleValue;
    }

    public String getFragranceValue() {
        return fragranceValue;
    }

    public void setFragranceValue(String fragranceValue) {
        this.fragranceValue = fragranceValue;
    }

    public String getAirPurification() {
        return airPurification;
    }

    public void setAirPurification(String airPurification) {
        this.airPurification = airPurification;
    }

    public String getPlantCombinations() {
        return plantCombinations;
    }

    public void setPlantCombinations(String plantCombinations) {
        this.plantCombinations = plantCombinations;
    }

    public String getPurchaseTips() {
        return purchaseTips;
    }

    public void setPurchaseTips(String purchaseTips) {
        this.purchaseTips = purchaseTips;
    }

    public String getBeginnerFriendly() {
        return beginnerFriendly;
    }

    public void setBeginnerFriendly(String beginnerFriendly) {
        this.beginnerFriendly = beginnerFriendly;
    }

    public String getPlacementSuggestion() {
        return placementSuggestion;
    }

    public void setPlacementSuggestion(String placementSuggestion) {
        this.placementSuggestion = placementSuggestion;
    }

    public String getCommonProblems() {
        return commonProblems;
    }

    public void setCommonProblems(String commonProblems) {
        this.commonProblems = commonProblems;
    }

    public String getYellowLeafCauses() {
        return yellowLeafCauses;
    }

    public void setYellowLeafCauses(String yellowLeafCauses) {
        this.yellowLeafCauses = yellowLeafCauses;
    }

    public String getNoFloweringCauses() {
        return noFloweringCauses;
    }

    public void setNoFloweringCauses(String noFloweringCauses) {
        this.noFloweringCauses = noFloweringCauses;
    }

    public String getRootRotCauses() {
        return rootRotCauses;
    }

    public void setRootRotCauses(String rootRotCauses) {
        this.rootRotCauses = rootRotCauses;
    }

    public String getWinterCare() {
        return winterCare;
    }

    public void setWinterCare(String winterCare) {
        this.winterCare = winterCare;
    }

    public String getSummerCare() {
        return summerCare;
    }

    public void setSummerCare(String summerCare) {
        this.summerCare = summerCare;
    }

    public String getLifespan() {
        return lifespan;
    }

    public void setLifespan(String lifespan) {
        this.lifespan = lifespan;
    }

    public String getGrowthRate() {
        return growthRate;
    }

    public void setGrowthRate(String growthRate) {
        this.growthRate = growthRate;
    }

    public String getIsToxic() {
        return isToxic;
    }

    public void setIsToxic(String isToxic) {
        this.isToxic = isToxic;
    }

    public String getToxicToHuman() {
        return toxicToHuman;
    }

    public void setToxicToHuman(String toxicToHuman) {
        this.toxicToHuman = toxicToHuman;
    }

    public String getToxicToPet() {
        return toxicToPet;
    }

    public void setToxicToPet(String toxicToPet) {
        this.toxicToPet = toxicToPet;
    }

    public String getIsEdible() {
        return isEdible;
    }

    public void setIsEdible(String isEdible) {
        this.isEdible = isEdible;
    }

    public String getIsCityFlower() {
        return isCityFlower;
    }

    public void setIsCityFlower(String isCityFlower) {
        this.isCityFlower = isCityFlower;
    }

    public String getCityFlowerOf() {
        return cityFlowerOf;
    }

    public void setCityFlowerOf(String cityFlowerOf) {
        this.cityFlowerOf = cityFlowerOf;
    }

    public String getFestivalUsage() {
        return festivalUsage;
    }

    public void setFestivalUsage(String festivalUsage) {
        this.festivalUsage = festivalUsage;
    }

    public String getOccasionUsage() {
        return occasionUsage;
    }

    public void setOccasionUsage(String occasionUsage) {
        this.occasionUsage = occasionUsage;
    }

    public String getSuitableSeason() {
        return suitableSeason;
    }

    public void setSuitableSeason(String suitableSeason) {
        this.suitableSeason = suitableSeason;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public Boolean getIsRecommended() {
        return isRecommended;
    }

    public void setIsRecommended(Boolean isRecommended) {
        this.isRecommended = isRecommended;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
