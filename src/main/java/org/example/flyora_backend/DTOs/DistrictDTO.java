package org.example.flyora_backend.DTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

@JsonIgnoreProperties(ignoreUnknown = true) 
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistrictDTO {
    @JsonProperty("DistrictID")
    private Integer DistrictID;

    @JsonProperty("DistrictName")
    private String DistrictName;
}
