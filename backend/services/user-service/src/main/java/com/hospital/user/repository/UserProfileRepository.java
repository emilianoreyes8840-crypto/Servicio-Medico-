package com.hospital.user.repository;

import com.hospital.user.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findByAuthUsername(String authUsername);

    boolean existsByAuthUsername(String authUsername);
}
