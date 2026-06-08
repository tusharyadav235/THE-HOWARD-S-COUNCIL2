package com.howardscouncil.controller;

import com.howardscouncil.model.Testimonial;
import com.howardscouncil.repository.TestimonialRepository;
import com.howardscouncil.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/testimonials")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TestimonialController {

    private final TestimonialRepository testimonialRepository;
    private final S3Service s3Service;

    @GetMapping
    public ResponseEntity<List<Testimonial>> getPublicTestimonials() {
        return ResponseEntity.ok(testimonialRepository.findByIsActiveTrueOrderBySortOrderAscCreatedAtDesc());
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Testimonial>> getAllTestimonials() {
        return ResponseEntity.ok(testimonialRepository.findAllByOrderBySortOrderAscCreatedAtDesc());
    }

    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Testimonial> createTestimonial(
            @RequestParam("studentName") String studentName,
            @RequestParam(value = "bandScore", required = false) String bandScore,
            @RequestParam(value = "courseName", required = false) String courseName,
            @RequestParam(value = "testimonialText", required = false) String testimonialText,
            @RequestParam(value = "rating", defaultValue = "5") Integer rating,
            @RequestParam(value = "sortOrder", defaultValue = "0") Integer sortOrder,
            @RequestParam(value = "photo", required = false) MultipartFile photo) throws IOException {

        Testimonial t = new Testimonial();
        t.setStudentName(studentName);
        t.setBandScore(bandScore);
        t.setCourseName(courseName);
        t.setTestimonialText(testimonialText);
        t.setRating(rating);
        t.setSortOrder(sortOrder);
        t.setIsActive(true);

        if (photo != null && !photo.isEmpty()) {
            S3Service.S3UploadResult result = s3Service.uploadFileWithKey(photo, "testimonials");
            t.setPhotoUrl(result.url());
            t.setPhotoS3Key(result.s3Key());
        }

        return ResponseEntity.ok(testimonialRepository.save(t));
    }

    @PatchMapping("/admin/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Testimonial> toggle(@PathVariable Long id) {
        Testimonial t = testimonialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
        t.setIsActive(!t.getIsActive());
        return ResponseEntity.ok(testimonialRepository.save(t));
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        Testimonial t = testimonialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
        if (t.getPhotoS3Key() != null) s3Service.deleteFile(t.getPhotoS3Key());
        testimonialRepository.delete(t);
        return ResponseEntity.ok(Map.of("success", true));
    }
}
