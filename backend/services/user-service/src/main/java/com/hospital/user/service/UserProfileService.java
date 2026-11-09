package com.hospital.user.service;

import com.hospital.user.dto.UserProfileRequest;
import com.hospital.user.dto.UserProfileResponse;

public interface UserProfileService {

    UserProfileResponse create(UserProfileRequest request);

    UserProfileResponse getByAuthUsername(String authUsername);

    UserProfileResponse update(String authUsername, UserProfileRequest request);
}
