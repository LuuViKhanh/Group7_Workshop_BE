package org.example.flyora_backend.dynamo.models;

import org.example.flyora_backend.dynamo.SafeIntegerConverter;

import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
@Setter
public class DeliveryNoteDynamoDB {
    private Integer id;
    private Integer orderId;
    private Integer shippingMethodId;
    private String deliveryPartnerName;
    private String trackingNumber;
    private String status;
    private String estimatedDeliveryDate;
    private String actualDeliveryDate;

    @DynamoDbPartitionKey @DynamoDbAttribute("id") @DynamoDbConvertedBy(SafeIntegerConverter.class)
    public Integer getId() { return id; }
    
    @DynamoDbAttribute("order_id") 
    @DynamoDbSecondaryPartitionKey(indexNames = "order_id-index")
    public Integer getOrderId() { return orderId; }

    @DynamoDbAttribute("shipping_method_id") public Integer getShippingMethodId() { return shippingMethodId; }
    @DynamoDbAttribute("delivery_partner_name") public String getDeliveryPartnerName() { return deliveryPartnerName; }
    
    @DynamoDbAttribute("tracking_number") 
    @DynamoDbSecondaryPartitionKey(indexNames = "tracking_number-index")
    public String getTrackingNumber() { return trackingNumber; }
    
    @DynamoDbAttribute("status") public String getStatus() { return status; }
    @DynamoDbAttribute("estimated_delivery_date") public String getEstimatedDeliveryDate() { return estimatedDeliveryDate; }
    @DynamoDbAttribute("actual_delivery_date") public String getActualDeliveryDate() { return actualDeliveryDate; }

}
