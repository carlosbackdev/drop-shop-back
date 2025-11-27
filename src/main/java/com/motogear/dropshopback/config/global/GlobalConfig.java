package com.motogear.dropshopback.config.global;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "global_config", schema = "shop")
public class GlobalConfig {
    @Id
    @Column(name = "config_key", unique = true)
    private String key;

    @Column(name = "config_value")
    private String value;

    @Column(name = "description")
    private String description;
}