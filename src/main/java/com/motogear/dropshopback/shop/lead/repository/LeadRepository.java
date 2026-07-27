package com.motogear.dropshopback.shop.lead.repository;

import com.motogear.dropshopback.shop.lead.domain.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {

    Optional<Lead> findByEmailIgnoreCaseAndProductSlug(String email, String productSlug);

    List<Lead> findAllByOrderByCreatedAtDesc();

    List<Lead> findByProductSlugOrderByCreatedAtDesc(String productSlug);
}
