package com.hospital.user.controller;

import com.hospital.user.dto.UserProfileRequest;
import com.hospital.user.dto.UserProfileResponse;
import com.hospital.user.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @PostMapping
    public ResponseEntity<UserProfileResponse> create(@Valid @RequestBody UserProfileRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userProfileService.create(request));
    }

    @GetMapping("/{authUsername}")
    public ResponseEntity<UserProfileResponse> getByAuthUsername(@PathVariable String authUsername) {
        return ResponseEntity.ok(userProfileService.getByAuthUsername(authUsername));
    }

    @PutMapping("/{authUsername}")
    public ResponseEntity<UserProfileResponse> update(@PathVariable String authUsername,
                                                       @Valid @RequestBody UserProfileRequest request) {
        return ResponseEntity.ok(userProfileService.update(authUsername, request));
    }
}
