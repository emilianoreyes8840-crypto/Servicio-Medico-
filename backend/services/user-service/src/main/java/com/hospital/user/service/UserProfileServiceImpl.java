package com.hospital.user.service;

import com.hospital.user.dto.UserProfileRequest;
import com.hospital.user.dto.UserProfileResponse;
import com.hospital.user.entity.UserProfile;
import com.hospital.user.exception.UserProfileAlreadyExistsException;
import com.hospital.user.exception.UserProfileNotFoundException;
import com.hospital.user.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;

    @Override
    @Transactional
    public UserProfileResponse create(UserProfileRequest request) {
        if (userProfileRepository.existsByAuthUsername(request.getAuthUsername())) {
            throw new UserProfileAlreadyExistsException(
                    "Ya existe un perfil para el authUsername: " + request.getAuthUsername());
        }

        UserProfile profile = UserProfile.builder()
                .authUsername(request.getAuthUsername())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .professionalLicense(request.getProfessionalLicense())
                .build();

        UserProfile saved = userProfileRepository.save(profile);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getByAuthUsername(String authUsername) {
        UserProfile profile = findOrThrow(authUsername);
        return toResponse(profile);
    }

    @Override
    @Transactional
    public UserProfileResponse update(String authUsername, UserProfileRequest request) {
        UserProfile profile = findOrThrow(authUsername);

        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setEmail(request.getEmail());
        profile.setPhoneNumber(request.getPhoneNumber());
        profile.setProfessionalLicense(request.getProfessionalLicense());

        UserProfile saved = userProfileRepository.save(profile);
        return toResponse(saved);
    }

    private UserProfile findOrThrow(String authUsername) {
        return userProfileRepository.findByAuthUsername(authUsername)
                .orElseThrow(() -> new UserProfileNotFoundException(
                        "No existe un perfil para el authUsername: " + authUsername));
    }

    private UserProfileResponse toResponse(UserProfile profile) {
        return UserProfileResponse.builder()
                .id(profile.getId())
                .authUsername(profile.getAuthUsername())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .email(profile.getEmail())
                .phoneNumber(profile.getPhoneNumber())
                .professionalLicense(profile.getProfessionalLicense())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}
