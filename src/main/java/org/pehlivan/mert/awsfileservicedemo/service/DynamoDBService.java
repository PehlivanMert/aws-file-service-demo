package org.pehlivan.mert.awsfileservicedemo.service;

import lombok.RequiredArgsConstructor;
import org.pehlivan.mert.awsfileservicedemo.model.FileMetadata;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DynamoDBService {
    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private static final String TABLE_NAME = "file-metadata";

    public void saveFileMetadata(FileMetadata metadata) {
        DynamoDbTable<FileMetadata> table = dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(FileMetadata.class));
        table.putItem(metadata);
    }

    public FileMetadata getFileMetadata(String id, String fileName) {
        DynamoDbTable<FileMetadata> table = dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(FileMetadata.class));
        Key key = Key.builder()
                .partitionValue(id)
                .sortValue(fileName)
                .build();
        return table.getItem(key);
    }

    public List<FileMetadata> getAllFileMetadata() {
        DynamoDbTable<FileMetadata> table = dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(FileMetadata.class));
        return table.scan().items().stream().collect(Collectors.toList());
    }

    public void deleteFileMetadata(String id, String fileName) {
        DynamoDbTable<FileMetadata> table = dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(FileMetadata.class));
        Key key = Key.builder()
                .partitionValue(id)
                .sortValue(fileName)
                .build();
        table.deleteItem(key);
    }
}