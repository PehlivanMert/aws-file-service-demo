package org.pehlivan.mert.awsfileservicedemo.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorResponse {
    private int code;
    private String message;
    private String path;
    private LocalDateTime timestamp;
}
