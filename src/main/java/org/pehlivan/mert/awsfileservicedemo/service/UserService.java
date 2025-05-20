package org.pehlivan.mert.awsfileservicedemo.service;


import lombok.RequiredArgsConstructor;

import org.pehlivan.mert.awsfileservicedemo.dto.UserCreateRequest;
import org.pehlivan.mert.awsfileservicedemo.dto.UserDto;
import org.pehlivan.mert.awsfileservicedemo.dto.UserProfilePhotoUpdateRequest;
import org.pehlivan.mert.awsfileservicedemo.dto.converter.UserConverter;
import org.pehlivan.mert.awsfileservicedemo.exception.UserAlreadyExist;
import org.pehlivan.mert.awsfileservicedemo.exception.UserNotFoundException;
import org.pehlivan.mert.awsfileservicedemo.model.User;
import org.pehlivan.mert.awsfileservicedemo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class UserService {

    private final S3Service s3Service;
    private final UserRepository userRepository;
    private final UserConverter userConverter;

    public UserDto createUser(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExist(String.format("User with email %s already exists", request.getEmail()));
        }

        String profilePhotoKey = s3Service.uploadFile(request.getProfilePhoto());

        User newUser = User.create(request, profilePhotoKey);

        return userConverter.toUserDto(userRepository.save(newUser));
    }

    public UserDto findByEmail(String email) {
        User userFromDb = userRepository.findByEmail(email).orElseThrow(
                () -> new UserNotFoundException(String.format("User not found with email %s", email)));
        return userConverter.toUserDto(userFromDb);
    }

    public UserDto updateProfilePhoto(Long id, UserProfilePhotoUpdateRequest request) {
        User userFromDb = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(String.format("User not found with id %s", id)));

        if (!ObjectUtils.isEmpty(userFromDb.getProfilePhotoKey())) {
            s3Service.deleteFile(userFromDb.getProfilePhotoKey());
        }

        String updatedProfilePhotoKey = s3Service.uploadFile(request.getProfilePhoto());
        userFromDb.setProfilePhotoKey(updatedProfilePhotoKey);
        return userConverter.toUserDto(userRepository.save(userFromDb));
    }
}
