package com.howardscouncil.controller;

import com.howardscouncil.dto.GalleryImageDTO;
import com.howardscouncil.model.GalleryImage;
import com.howardscouncil.service.GalleryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/gallery")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GalleryController {

    private final GalleryService galleryService;

    // ─── PUBLIC ENDPOINTS ────────────────────────────────────────────────────

    /**
     * GET /api/gallery — returns active images for the public website
     */
    @GetMapping
    public ResponseEntity<List<GalleryImage>> getPublicGallery() {
        return ResponseEntity.ok(galleryService.getActiveGalleryImages());
    }

    /**
     * GET /api/gallery/category/{cat} — filter by category
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<GalleryImage>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(galleryService.getImagesByCategory(category));
    }

    // ─── ADMIN ENDPOINTS (JWT protected) ─────────────────────────────────────

    /**
     * GET /api/gallery/admin/all — all images including inactive
     */
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<GalleryImage>> getAllImages() {
        return ResponseEntity.ok(galleryService.getAllGalleryImages());
    }

    /**
     * POST /api/gallery/admin/upload — upload image to S3 + save to MySQL
     *
     * Form-data fields:
     *   file        (required) — the image file
     *   title       (required) — display title
     *   description (optional) — description
     *   category    (optional) — classroom | speaking | award | activity | general
     *   sortOrder   (optional) — integer for ordering
     */
    @PostMapping("/admin/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "category", defaultValue = "general") String category,
            @RequestParam(value = "sortOrder", defaultValue = "0") Integer sortOrder
    ) {
        try {
            GalleryImage image = galleryService.uploadGalleryImage(file, title, description, category, sortOrder);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Image uploaded successfully",
                    "image", image
            ));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Failed to upload image: " + e.getMessage()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * PUT /api/gallery/admin/{id} — update metadata (no re-upload)
     */
    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GalleryImage> updateImage(
            @PathVariable Long id,
            @RequestBody GalleryImageDTO dto) {
        return ResponseEntity.ok(galleryService.updateImageMetadata(id, dto));
    }

    /**
     * PATCH /api/gallery/admin/{id}/toggle — toggle active/inactive
     */
    @PatchMapping("/admin/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GalleryImage> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(galleryService.toggleImageStatus(id));
    }

    /**
     * DELETE /api/gallery/admin/{id} — delete from S3 + MySQL
     */
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteImage(@PathVariable Long id) {
        galleryService.deleteGalleryImage(id);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Image deleted successfully"
        ));
    }
}
