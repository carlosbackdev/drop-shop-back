package com.motogear.dropshopback.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JobStatusDto {
    private String jobId;
    private String status;
}
