package org.example.flyora_backend.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TrackOrderRequestDTO {
    @JsonProperty("order_code")
    private String order_code;
}
