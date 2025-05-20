package org.pehlivan.mert.awsfileservicedemo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserProfilePhotoUpdateRequest {
    @NotNull(message = "Profile photo is required")
    private MultipartFile profilePhoto;
}
