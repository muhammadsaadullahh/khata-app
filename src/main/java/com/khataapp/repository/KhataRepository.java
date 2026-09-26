package com.khataapp.repository;

import com.khataapp.model.KhataItem;
import com.khataapp.model.ItemType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import java.util.List;
import java.util.Optional;

@Repository
public class KhataRepository {
    private final DynamoDbTable<KhataItem> table;

    public KhataRepository(DynamoDbEnhancedClient client,
                           @Value("${aws.dynamodb.table-name:Khataapp}") String tableName) {
        this.table = client.table(tableName, TableSchema.fromBean(KhataItem.class));
    }

    public KhataItem save(KhataItem item) {
        table.putItem(item);
        return item;
    }

    public KhataItem saveUser(KhataItem user) {
        table.putItem(user);
        KhataItem usernameLookup = new KhataItem();
        usernameLookup.setPk("USERNAME#" + user.getUsername());
        usernameLookup.setSk("USER");
        usernameLookup.setItemType(ItemType.USER_LOOKUP.name());
        usernameLookup.setUserId(user.getUserId());
        usernameLookup.setUsername(user.getUsername());
        table.putItem(usernameLookup);
        return user;
    }

    public Optional<KhataItem> findUserByUsername(String username) {
        return table.query(r -> r.queryConditional(
                        software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional.keyEqualTo(
                                Key.builder().partitionValue("USERNAME#" + username).build())))
                .items().stream()
                .filter(item -> ItemType.USER_LOOKUP.name().equals(item.getItemType()))
                .map(item -> findUserById(item.getUserId()).orElse(null))
                .filter(java.util.Objects::nonNull)
                .findFirst();
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
                        software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional.keyEqualTo(
                                Key.builder().partitionValue("USER#" + userId).build())))
                .items().stream()
                .filter(item -> "TRANSACTION".equals(item.getItemType()))
                .toList();
    }
}
