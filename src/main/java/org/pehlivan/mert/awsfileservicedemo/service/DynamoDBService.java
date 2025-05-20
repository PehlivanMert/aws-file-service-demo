package org.pehlivan.mert.awsfileservicedemo.service;

import lombok.RequiredArgsConstructor;
import org.pehlivan.mert.awsfileservicedemo.exception.DynamoDBException;
import org.pehlivan.mert.awsfileservicedemo.exception.FileMetadataNotFoundException;
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
        try {
            DynamoDbTable<FileMetadata> table = dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(FileMetadata.class));
            table.putItem(metadata);
        } catch (Exception e) {
            throw new DynamoDBException("Failed to save file metadata: " + e.getMessage());
        }
    }

    public FileMetadata getFileMetadata(String id, String fileName) {
        try {
            DynamoDbTable<FileMetadata> table = dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(FileMetadata.class));
            Key key = Key.builder()
                    .partitionValue(id)
                    .sortValue(fileName)
                    .build();
            FileMetadata metadata = table.getItem(key);
            if (metadata == null) {
                throw new FileMetadataNotFoundException("File metadata not found for id: " + id + " and fileName: " + fileName);
            }
            return metadata;
        } catch (FileMetadataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DynamoDBException("Failed to get file metadata: " + e.getMessage());
        }
    }

    public List<FileMetadata> getAllFileMetadata() {
        try {
            DynamoDbTable<FileMetadata> table = dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(FileMetadata.class));
            return table.scan().items().stream().collect(Collectors.toList());
        } catch (Exception e) {
            throw new DynamoDBException("Failed to get all file metadata: " + e.getMessage());
        }
    }

    public void deleteFileMetadata(String id, String fileName) {
        try {
            DynamoDbTable<FileMetadata> table = dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(FileMetadata.class));
            Key key = Key.builder()
                    .partitionValue(id)
                    .sortValue(fileName)
                    .build();
            table.deleteItem(key);
        } catch (Exception e) {
            throw new DynamoDBException("Failed to delete file metadata: " + e.getMessage());
        }
    }
}