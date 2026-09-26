package com.khataapp.repository;

import com.khataapp.model.KhataItem;
import com.khataapp.model.ItemType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import java.util.List;
import java.util.Optional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

@Repository
public class KhataRepository {
    private final DynamoDbTable<KhataItem> table;

    public KhataRepository(DynamoDbEnhancedClient client,
                           @Value("${aws.dynamodb.table-name}") String tableName) {
        this.table = client.table(tableName, TableSchema.fromBean(KhataItem.class));
    }

    public KhataItem save(KhataItem item) {
        table.putItem(item);
        return item;
    }

    public boolean hasUsers() {
        return table.scan().items().stream().anyMatch(i -> ItemType.USER.name().equals(i.getItemType()));
    }

    public List<KhataItem> findCategories(String userId) {
        return table.query(r -> r.queryConditional(QueryConditional.sortBeginsWith(
                Key.builder().partitionValue("USER#" + userId).sortValue("CATEGORY#").build()))).items().stream().toList();
    }

    public KhataItem saveUser(KhataItem user) {
        KhataItem usernameLookup = new KhataItem();
        usernameLookup.setPk("USERNAME#" + user.getUsername());
        usernameLookup.setSk("USER");
        usernameLookup.setItemType(ItemType.USER_LOOKUP.name());
        usernameLookup.setUserId(user.getUserId());
        usernameLookup.setUsername(user.getUsername());
        try {
            table.putItem(r -> r.item(usernameLookup)
                    .conditionExpression(Expression.builder()
                            .expression("attribute_not_exists(PK)")
                            .build()));
        } catch (ConditionalCheckFailedException ex) {
            throw new com.khataapp.exception.ConflictException("Username is already registered");
        }
        table.putItem(user);
        return user;
    }

    public void saveUsernameLookup(String username, String userId) {
        KhataItem lookup = new KhataItem();
        lookup.setPk("USERNAME#" + username); lookup.setSk("USER");
        lookup.setItemType(ItemType.USER_LOOKUP.name()); lookup.setUserId(userId); lookup.setUsername(username);
        try {
            table.putItem(r -> r.item(lookup).conditionExpression(Expression.builder().expression("attribute_not_exists(PK)").build()));
        } catch (ConditionalCheckFailedException ex) {
            throw new com.khataapp.exception.ConflictException("Username is already registered");
        }
    }

    public Optional<KhataItem> findUserByUsername(String username) {
        KhataItem lookup = table.getItem(Key.builder()
                .partitionValue("USERNAME#" + username)
                .sortValue("USER")
                .build());
        if (lookup == null || !ItemType.USER_LOOKUP.name().equals(lookup.getItemType())) {
            return Optional.empty();
        }
        return findUserById(lookup.getUserId());
    }

    public Optional<KhataItem> findUserById(String userId) {
        KhataItem item = table.getItem(Key.builder()
                .partitionValue("USER#" + userId)
                .sortValue("METADATA")
                .build());
        return Optional.ofNullable(item);
    }

    public List<KhataItem> findTransactionsByUserId(String userId) {
        return table.query(r -> r.queryConditional(
                        software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional.sortBeginsWith(
                                Key.builder().partitionValue("USER#" + userId).sortValue("TXN#").build())))
                .items().stream()
                .toList();
    }

    public Optional<KhataItem> findTransactionById(String userId, String transactionId) {
        return table.query(r -> r.queryConditional(
                        software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional.sortBeginsWith(
                                Key.builder().partitionValue("USER#" + userId).sortValue("TXN#").build()))
                .filterExpression(software.amazon.awssdk.enhanced.dynamodb.Expression.builder()
                        .expression("transactionId = :transactionId")
                        .expressionValues(java.util.Map.of(
                                ":transactionId", software.amazon.awssdk.services.dynamodb.model.AttributeValue.builder()
                                        .s(transactionId).build()))
                        .build()))
                .items().stream().findFirst();
    }

    public void delete(KhataItem item) {
        table.deleteItem(Key.builder()
                .partitionValue(item.getPk())
                .sortValue(item.getSk())
                .build());
    }
}
