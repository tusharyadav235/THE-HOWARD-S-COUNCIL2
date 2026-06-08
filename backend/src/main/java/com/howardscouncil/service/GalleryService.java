package com.howardscouncil.service;

import com.howardscouncil.dto.GalleryImageDTO;
import com.howardscouncil.model.GalleryImage;
import com.howardscouncil.repository.GalleryImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GalleryService {

    private final GalleryImageRepository galleryImageRepository;
    private final S3Service s3Service;

    private static final List<String> ALLOWED_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/webp", "image/gif"
    );

    private static final long MAX_SIZE_BYTES = 10 * 1024 * 1024; // 10MB

    /**
     * Upload image to S3 and save metadata to MySQL
     */
    public GalleryImage uploadGalleryImage(MultipartFile file, String title,
                                           String description, String category,
                                           Integer sortOrder) throws IOException {
        // Validate file
        validateFile(file);

        // Upload to S3 under gallery/ folder
        S3Service.S3UploadResult result = s3Service.uploadFileWithKey(file, "gallery");

        // Save metadata to MySQL
        GalleryImage image = new GalleryImage();
        image.setTitle(title);
        image.setDescription(description);
        image.setImageUrl(result.url());
        image.setS3Key(result.s3Key());
        image.setCategory(category != null ? category : "general");
        image.setIsActive(true);
        image.setSortOrder(sortOrder != null ? sortOrder : 0);

        GalleryImage saved = galleryImageRepository.save(image);
        log.info("✅ Gallery image saved: id={}, url={}", saved.getId(), saved.getImageUrl());
        return saved;
    }

    /**
     * Get all active gallery images for public display
     */
    public List<GalleryImage> getActiveGalleryImages() {
        return galleryImageRepository.findByIsActiveTrueOrderBySortOrderAscCreatedAtDesc();
    }

    /**
     * Get all gallery images (admin view)
     */
    public List<GalleryImage> getAllGalleryImages() {
        return galleryImageRepository.findAllByOrderBySortOrderAscCreatedAtDesc();
    }

    /**
     * Get images by category
     */
    public List<GalleryImage> getImagesByCategory(String category) {
        return galleryImageRepository.findByCategoryAndIsActiveTrueOrderBySortOrderAsc(category);
    }

    /**
     * Toggle image visibility
     */
    public GalleryImage toggleImageStatus(Long id) {
        GalleryImage image = galleryImageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gallery image not found: " + id));
        image.setIsActive(!image.getIsActive());
        return galleryImageRepository.save(image);
    }

    /**
     * Update image metadata (title, description, category, sortOrder)
     */
    public GalleryImage updateImageMetadata(Long id, GalleryImageDTO dto) {
        GalleryImage image = galleryImageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gallery image not found: " + id));

        if (dto.getTitle() != null)       image.setTitle(dto.getTitle());
        if (dto.getDescription() != null) image.setDescription(dto.getDescription());
        if (dto.getCategory() != null)    image.setCategory(dto.getCategory());
        if (dto.getSortOrder() != null)   image.setSortOrder(dto.getSortOrder());

        return galleryImageRepository.save(image);
    }

    /**
     * Delete image from S3 and MySQL
     */
    public void deleteGalleryImage(Long id) {
        GalleryImage image = galleryImageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gallery image not found: " + id));

        // Delete from S3
        s3Service.deleteFile(image.getS3Key());

        // Delete from MySQL
        galleryImageRepository.delete(image);
        log.info("✅ Gallery image deleted: id={}", id);
    }

    // ─── Private helpers ─────────────────────────────────────────────────────

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty or null");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new RuntimeException("Invalid file type. Allowed: JPEG, PNG, WEBP, GIF");
        }
        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new RuntimeException("File size exceeds 10MB limit");
        }
    }
}
