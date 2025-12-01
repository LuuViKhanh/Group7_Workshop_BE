package org.example.flyora_backend.DTOs;

import java.util.List;

import com.fasterxml.jackson.annotation.*;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CreateOrderRequestDTO {

    //--- Thông tin người nhận ---
    @JsonProperty("to_name")
    private String to_name;
    @JsonProperty("to_phone")
    private String to_phone;
    @JsonProperty("to_address")
    private String to_address; // Địa chỉ chi tiết, số nhà, tên đường
    @JsonProperty("to_ward_code")
    private String to_ward_code;
    @JsonProperty("to_district_id")
    private int to_district_id;

    //--- Thông tin gói hàng và thanh toán ---
    @JsonProperty("cod_amount")
    private int cod_amount; // Số tiền cần thu hộ từ khách. Nếu khách đã thanh toán online, đặt là 0.

    private String content; // Nội dung đơn hàng (ví dụ: "Quần áo thời trang")
    private int weight;
    private int length;
    private int width;
    private int height;

    @JsonProperty("insurance_value")
    private int insurance_value; // Giá trị thực của đơn hàng (để GHN đền bù nếu mất)

    @JsonProperty("service_id")
    private int service_id; // ID dịch vụ đã chọn lúc tính phí

    @JsonProperty("payment_type_id")
    private int payment_type_id; // Ai trả phí ship? 1: Shop/Người bán, 2: Khách/Người mua

    private String note; // Ghi chú cho shipper

    @JsonProperty("required_note")
    private String required_note; // Yêu cầu lúc giao: "KHONGCHOXEMHANG", "CHOXEMHANGKHONGTHU", "CHOTHUHANG"

    //--- Danh sách sản phẩm trong đơn hàng ---
    private List<ItemDTO> items;
}
