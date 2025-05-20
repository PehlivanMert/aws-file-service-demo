package org.pehlivan.mert.awsfileservicedemo.model;

import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.time.LocalDateTime;

@Data
@DynamoDbBean
public class FileMetadata {
    private String id;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String s3Url;
    private LocalDateTime uploadDate;
    private String status;

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }

    @DynamoDbSortKey
    public String getFileName() {
        return fileName;
    }
}