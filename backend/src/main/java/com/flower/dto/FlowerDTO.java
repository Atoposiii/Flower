package com.flower.dto;

import lombok.Data;

@Data
public class FlowerDTO {
    private String name;
    private String scientificName;
    private String family;
    private String genus;
    private String description;
    private String habitat;
    private String floweringPeriod;
    private String imageUrl;
}
