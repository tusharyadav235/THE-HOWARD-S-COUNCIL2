package com.howardscouncil.controller;

import com.howardscouncil.model.ContactMessage;
import com.howardscouncil.model.DemoEnquiry;
import com.howardscouncil.service.EnquiryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/enquiry")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EnquiryController {

    private final EnquiryService enquiryService;

    // ─── PUBLIC — Demo Form Submission ────────────────────────────────────────

    @PostMapping("/demo")
    public ResponseEntity<Map<String, Object>> submitDemoEnquiry(
            @Valid @RequestBody DemoEnquiry enquiry) {
        DemoEnquiry saved = enquiryService.saveDemoEnquiry(enquiry);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Demo class booking received! We'll contact you within 24 hours.",
                "id", saved.getId()
        ));
    }

    // ─── PUBLIC — Contact Form Submission ────────────────────────────────────

    @PostMapping("/contact")
    public ResponseEntity<Map<String, Object>> submitContact(
            @Valid @RequestBody ContactMessage message) {
        ContactMessage saved = enquiryService.saveContactMessage(message);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Message received! We'll get back to you soon.",
                "id", saved.getId()
        ));
    }

    // ─── ADMIN — Enquiries ────────────────────────────────────────────────────

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DemoEnquiry>> getAllEnquiries() {
        return ResponseEntity.ok(enquiryService.getAllEnquiries());
    }

    @GetMapping("/admin/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DemoEnquiry>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(enquiryService.getEnquiriesByStatus(status));
    }

    @PatchMapping("/admin/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DemoEnquiry> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(enquiryService.updateEnquiryStatus(id, body.get("status")));
    }

    @PatchMapping("/admin/{id}/notes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DemoEnquiry> updateNotes(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(enquiryService.updateEnquiryNotes(id, body.get("notes")));
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteEnquiry(@PathVariable Long id) {
        enquiryService.deleteEnquiry(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Enquiry deleted"));
    }

    // ─── ADMIN — Contact Messages ─────────────────────────────────────────────

    @GetMapping("/admin/messages")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ContactMessage>> getAllMessages() {
        return ResponseEntity.ok(enquiryService.getAllMessages());
    }

    @GetMapping("/admin/messages/unread")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ContactMessage>> getUnreadMessages() {
        return ResponseEntity.ok(enquiryService.getUnreadMessages());
    }

    @PatchMapping("/admin/messages/{id}/read")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ContactMessage> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(enquiryService.markAsRead(id));
    }

    @DeleteMapping("/admin/messages/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteMessage(@PathVariable Long id) {
        enquiryService.deleteMessage(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Message deleted"));
    }

    // ─── ADMIN — Dashboard Stats ──────────────────────────────────────────────

    @GetMapping("/admin/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(enquiryService.getDashboardStats());
    }
}
