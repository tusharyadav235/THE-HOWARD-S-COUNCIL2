package com.howardscouncil.dto;

import lombok.Data;

@Data
public class GalleryImageDTO {
    private String title;
    private String description;
    private String category;
    private Integer sortOrder;
    private Boolean isActive;
}
