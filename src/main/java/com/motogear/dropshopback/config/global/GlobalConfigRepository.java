package com.motogear.dropshopback.config.global;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GlobalConfigRepository extends JpaRepository<GlobalConfig, String> {
    Optional<GlobalConfig> findByKey(String key);
}
