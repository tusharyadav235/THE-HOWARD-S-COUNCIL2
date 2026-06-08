package com.howardscouncil.repository;

import com.howardscouncil.model.DemoEnquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DemoEnquiryRepository extends JpaRepository<DemoEnquiry, Long> {
    List<DemoEnquiry> findAllByOrderByCreatedAtDesc();
    List<DemoEnquiry> findByStatusOrderByCreatedAtDesc(DemoEnquiry.EnquiryStatus status);
    long countByStatus(DemoEnquiry.EnquiryStatus status);
}
