package com.motogear.dropshopback.shop.catalog.repository;

import com.motogear.dropshopback.shop.catalog.domain.HomeBanner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HomeBannerRepository extends JpaRepository<HomeBanner,Long> {
}
