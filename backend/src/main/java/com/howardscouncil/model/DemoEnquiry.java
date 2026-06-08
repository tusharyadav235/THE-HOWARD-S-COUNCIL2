package com.howardscouncil.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "demo_enquiries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemoEnquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_name", nullable = false)
    private String studentName;

    @Column(nullable = false)
    private Integer age;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "course_interested")
    private String courseInterested;

    @Column(name = "preferred_timing")
    private String preferredTiming;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EnquiryStatus status = EnquiryStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum EnquiryStatus {
        PENDING, CONTACTED, ENROLLED, CANCELLED
    }
}
