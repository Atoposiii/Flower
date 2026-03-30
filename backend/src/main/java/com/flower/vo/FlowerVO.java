package com.flower.vo;

import lombok.Data;

@Data
public class FlowerVO {
    private Integer id;
    private String name;
    private String scientificName;
    private String family;
    private String genus;
    private String description;
    private String habitat;
    private String floweringPeriod;
    private String imageUrl;
}
