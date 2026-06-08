package com.howardscouncil.repository;

import com.howardscouncil.model.Testimonial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestimonialRepository extends JpaRepository<Testimonial, Long> {
    List<Testimonial> findByIsActiveTrueOrderBySortOrderAscCreatedAtDesc();
    List<Testimonial> findAllByOrderBySortOrderAscCreatedAtDesc();
}
