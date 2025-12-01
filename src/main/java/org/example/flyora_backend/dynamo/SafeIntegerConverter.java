package org.example.flyora_backend.dynamo;

import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class SafeIntegerConverter implements AttributeConverter<Integer> {

    @Override
    public AttributeValue transformFrom(Integer input) {
        return input != null ? AttributeValue.builder().n(String.valueOf(input)).build() : AttributeValue.builder().nul(true).build();
    }

    @Override
    public Integer transformTo(AttributeValue input) {
        if (input.n() == null || "NULL".equalsIgnoreCase(input.n())) {
            return 0; // hoặc null tuỳ logic
        }
        return Integer.valueOf(input.n());
    }

    @Override
    public EnhancedType<Integer> type() {
        return EnhancedType.of(Integer.class);
    }

    @Override
    public software.amazon.awssdk.enhanced.dynamodb.AttributeValueType attributeValueType() {
        return software.amazon.awssdk.enhanced.dynamodb.AttributeValueType.N;
    }
}
