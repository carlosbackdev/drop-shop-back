package com.motogear.dropshopback.shop.banner.repository;

import com.motogear.dropshopback.shop.banner.domain.HomeBanner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HomeBannerRepository extends JpaRepository<HomeBanner,Long> {
}
