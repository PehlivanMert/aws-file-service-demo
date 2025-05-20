package org.pehlivan.mert.awsfileservicedemo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

@Service
@RequiredArgsConstructor
public class SNSService {
    private final SnsClient snsClient;

    @Value("${aws.sns.topic-arn}")
    private String topicArn;

    public String publishMessage(String message) {
        try {
            PublishRequest request = PublishRequest.builder()
                    .topicArn(topicArn)
                    .message(message)
                    .build();

            PublishResponse response = snsClient.publish(request);
            return response.messageId();
        } catch (Exception e) {
            throw new RuntimeException("Error publishing message to SNS: " + e.getMessage(), e);
        }
    }

    public String publishMessageWithSubject(String subject, String message) {
        try {
            PublishRequest request = PublishRequest.builder()
                    .topicArn(topicArn)
                    .subject(subject)
                    .message(message)
                    .build();

            PublishResponse response = snsClient.publish(request);
            return response.messageId();
        } catch (Exception e) {
            throw new RuntimeException("Error publishing message to SNS: " + e.getMessage(), e);
        }
    }
}