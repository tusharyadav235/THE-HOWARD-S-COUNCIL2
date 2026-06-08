package com.howardscouncil.repository;

import com.howardscouncil.model.GalleryImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GalleryImageRepository extends JpaRepository<GalleryImage, Long> {
    List<GalleryImage> findByIsActiveTrueOrderBySortOrderAscCreatedAtDesc();
    List<GalleryImage> findByCategoryAndIsActiveTrueOrderBySortOrderAsc(String category);
    List<GalleryImage> findAllByOrderBySortOrderAscCreatedAtDesc();
}
