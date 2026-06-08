package com.howardscouncil.service;

import com.howardscouncil.model.ContactMessage;
import com.howardscouncil.model.DemoEnquiry;
import com.howardscouncil.repository.ContactMessageRepository;
import com.howardscouncil.repository.DemoEnquiryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnquiryService {

    private final DemoEnquiryRepository demoEnquiryRepository;
    private final ContactMessageRepository contactMessageRepository;

    // ─── Demo Enquiry ─────────────────────────────────────────────────────────

    public DemoEnquiry saveDemoEnquiry(DemoEnquiry enquiry) {
        enquiry.setStatus(DemoEnquiry.EnquiryStatus.PENDING);
        DemoEnquiry saved = demoEnquiryRepository.save(enquiry);
        log.info("✅ Demo enquiry saved: name={}, phone={}", saved.getStudentName(), saved.getPhoneNumber());
        return saved;
    }

    public List<DemoEnquiry> getAllEnquiries() {
        return demoEnquiryRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<DemoEnquiry> getEnquiriesByStatus(String status) {
        DemoEnquiry.EnquiryStatus enumStatus = DemoEnquiry.EnquiryStatus.valueOf(status.toUpperCase());
        return demoEnquiryRepository.findByStatusOrderByCreatedAtDesc(enumStatus);
    }

    public DemoEnquiry updateEnquiryStatus(Long id, String status) {
        DemoEnquiry enquiry = demoEnquiryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enquiry not found: " + id));
        enquiry.setStatus(DemoEnquiry.EnquiryStatus.valueOf(status.toUpperCase()));
        return demoEnquiryRepository.save(enquiry);
    }

    public DemoEnquiry updateEnquiryNotes(Long id, String notes) {
        DemoEnquiry enquiry = demoEnquiryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enquiry not found: " + id));
        enquiry.setNotes(notes);
        return demoEnquiryRepository.save(enquiry);
    }

    public void deleteEnquiry(Long id) {
        demoEnquiryRepository.deleteById(id);
    }

    // ─── Contact Messages ─────────────────────────────────────────────────────

    public ContactMessage saveContactMessage(ContactMessage message) {
        ContactMessage saved = contactMessageRepository.save(message);
        log.info("✅ Contact message saved: name={}", saved.getName());
        return saved;
    }

    public List<ContactMessage> getAllMessages() {
        return contactMessageRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<ContactMessage> getUnreadMessages() {
        return contactMessageRepository.findByIsReadFalseOrderByCreatedAtDesc();
    }

    public ContactMessage markAsRead(Long id) {
        ContactMessage message = contactMessageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found: " + id));
        message.setIsRead(true);
        return contactMessageRepository.save(message);
    }

    public void deleteMessage(Long id) {
        contactMessageRepository.deleteById(id);
    }

    // ─── Dashboard Stats ──────────────────────────────────────────────────────

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalEnquiries", demoEnquiryRepository.count());
        stats.put("pendingEnquiries", demoEnquiryRepository.countByStatus(DemoEnquiry.EnquiryStatus.PENDING));
        stats.put("enrolledStudents", demoEnquiryRepository.countByStatus(DemoEnquiry.EnquiryStatus.ENROLLED));
        stats.put("totalMessages", contactMessageRepository.count());
        stats.put("unreadMessages", contactMessageRepository.countByIsReadFalse());
        return stats;
    }
}
