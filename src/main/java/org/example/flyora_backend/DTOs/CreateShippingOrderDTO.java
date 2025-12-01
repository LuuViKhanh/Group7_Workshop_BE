package org.example.flyora_backend.DTOs;

import lombok.Data;

@Data
public class CreateShippingOrderDTO {
    private Integer orderId;
    private Integer shippingMethodId;
    private String toName;
    private String toPhone;
    private String toAddress;
    private String toDistrict;
    private String toWard;
    private String toProvince;
    private Integer requesterId;
}