package org.example.flyora_backend.DTOs;

public class PaymentCallbackDTO {
    private String paymentCode;

    public PaymentCallbackDTO() {}

    public PaymentCallbackDTO(String paymentCode) {
        this.paymentCode = paymentCode;
    }

    public String getPaymentCode() {
        return paymentCode;
    }

    public void setPaymentCode(String paymentCode) {
        this.paymentCode = paymentCode;
    }
}
