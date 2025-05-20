package org.pehlivan.mert.awsfileservicedemo.controller;

import lombok.RequiredArgsConstructor;
import org.pehlivan.mert.awsfileservicedemo.model.FileMetadata;
import org.pehlivan.mert.awsfileservicedemo.service.CacheService;
import org.pehlivan.mert.awsfileservicedemo.service.DynamoDBService;
import org.pehlivan.mert.awsfileservicedemo.service.S3Service;
import org.pehlivan.mert.awsfileservicedemo.service.SNSService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/aws")
@RequiredArgsConstructor
public class AWSController {
    private final DynamoDBService dynamoDBService;
    private final CacheService cacheService;
    private final SNSService snsService;
    private final S3Service s3Service;

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("AWS Controller is working!");
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // S3'e dosyayı yükle
            String s3Url = s3Service.uploadFile(file);

            // DynamoDB'ye metadata kaydet
            FileMetadata metadata = new FileMetadata();
            metadata.setId(UUID.randomUUID().toString());
            metadata.setFileName(file.getOriginalFilename());
            metadata.setFileType(file.getContentType());
            metadata.setFileSize(file.getSize());
            metadata.setS3Url(s3Url);
            metadata.setStatus("UPLOADED");
            dynamoDBService.saveFileMetadata(metadata);

            // Cache'e kaydet
            cacheService.set("file:" + metadata.getId(), metadata);

            // SNS bildirimi gönder
            snsService.publishMessageWithSubject(
                    "File Uploaded",
                    "File " + file.getOriginalFilename() + " has been uploaded successfully."
            );

            return ResponseEntity.ok("File uploaded successfully: " + s3Url);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error uploading file: " + e.getMessage());
        }
    }

    @GetMapping("/files")
    public ResponseEntity<List<FileMetadata>> getAllFiles() {
        return ResponseEntity.ok(dynamoDBService.getAllFileMetadata());
    }

    @GetMapping("/files/{id}")
    public ResponseEntity<FileMetadata> getFile(@PathVariable String id, @RequestParam String fileName) {
        // Önce cache'den kontrol et
        Object cachedFile = cacheService.get("file:" + id);
        if (cachedFile != null) {
            return ResponseEntity.ok((FileMetadata) cachedFile);
        }

        // Cache'de yoksa DynamoDB'den al
        FileMetadata file = dynamoDBService.getFileMetadata(id, fileName);
        if (file != null) {
            // Cache'e kaydet
            cacheService.set("file:" + id, file);
        }
        return ResponseEntity.ok(file);
    }

    @DeleteMapping("/files/{id}")
    public ResponseEntity<String> deleteFile(@PathVariable String id, @RequestParam String fileName) {
        try {
            // S3'ten dosyayı sil
            s3Service.deleteFile(fileName);

            // DynamoDB'den metadata'yı sil
            dynamoDBService.deleteFileMetadata(id, fileName);

            // Cache'den sil
            cacheService.delete("file:" + id);

            // SNS bildirimi gönder
            snsService.publishMessageWithSubject(
                    "File Deleted",
                    "File " + fileName + " has been deleted successfully."
            );

            return ResponseEntity.ok("File deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error deleting file: " + e.getMessage());
        }
    }
}