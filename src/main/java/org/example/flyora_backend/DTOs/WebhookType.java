// package org.example.flyora_backend.DTOs;

// public class WebhookType {
//     // Thêm các trường phù hợp với dữ liệu webhook bạn nhận được
//     // Ví dụ:
//     private String event;
//     private String data;

//     public WebhookType() {}

//     public String getEvent() {
//         return event;
//     }
//     public void setEvent(String event) {
//         this.event = event;
//     }
//     public String getData() {
//         return data;
//     }
//     public void setData(String data) {
//         this.data = data;
//     }
// }

package org.example.flyora_backend.DTOs;

import lombok.Data;
import vn.payos.type.WebhookData;

@Data
public class WebhookType {
    // Thêm các trường phù hợp với dữ liệu webhook bạn nhận được
    // Ví dụ:
    // private String event;
    // private String data;

    // public WebhookType() {}

    // public String getEvent() {
    // return event;
    // }
    // public void setEvent(String event) {
    // this.event = event;
    // }
    // public String getData() {
    // return data;
    // }
    // public void setData(String data) {
    // this.data = data;
    // }
    private String code;
    private String desc;
    private boolean success;
    private WebhookData data;
    private String signature;








    // private String orderCode;
    // private String status;
    // private Integer amount;
    // private String description;

    // private String accountNumber;
    // private String reference;
    // private String transactionDateTime;
    // private String currency;
    // private String paymentLinkId;
    // private String counterAccountBankId;
    // private String counterAccountBankName;
    // private String counterAccountName;
    // private String counterAccountNumber;
    // private String virtualAccountName;
    // private String virtualAccountNumber;
    // private boolean success;
    // private WebhookData data;

}
