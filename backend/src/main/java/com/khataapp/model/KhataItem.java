package com.khataapp.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@DynamoDbBean
public class KhataItem {
    private String pk;
    private String sk;
    private String itemType;
    private String userId;
    private String fullName;
    private String username;
    private String passwordHash;
    private String email;
    private String currency;
    private String role;
    private String categoryId;
    private Boolean systemCategory;
    private String transactionId;
    private String partyName;
    private String transactionType;
    private BigDecimal amount;
    private String category;
    private String note;
    private String createdAt;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("PK")
    public String getPk() { return pk; }

    @DynamoDbSortKey
    @DynamoDbAttribute("SK")
    public String getSk() { return sk; }
}
