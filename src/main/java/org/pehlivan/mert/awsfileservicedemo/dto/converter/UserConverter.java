package org.pehlivan.mert.awsfileservicedemo.dto.converter;

import lombok.RequiredArgsConstructor;
import org.pehlivan.mert.awsfileservicedemo.dto.UserDto;
import org.pehlivan.mert.awsfileservicedemo.model.User;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserConverter {

    public UserDto toUserDto(User user) {
        return UserDto.builder()
                .email(user.getEmail())
                .profilePhotoKey(user.getProfilePhotoKey())
                .build();
    }
}
