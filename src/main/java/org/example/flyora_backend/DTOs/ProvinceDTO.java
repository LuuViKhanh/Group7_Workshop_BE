package org.example.flyora_backend.DTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

@JsonIgnoreProperties(ignoreUnknown = true) 
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProvinceDTO {

    // Giữ nguyên các thuộc tính bạn cần
    @JsonProperty("ProvinceID")
    private Integer ProvinceID;

    @JsonProperty("ProvinceName")
    private String ProvinceName;
}
