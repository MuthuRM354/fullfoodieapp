package com.foodieapp.user.service;

import com.foodieapp.user.dto.ProfileRequest;
import com.foodieapp.user.model.UserProfile;
import com.foodieapp.user.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;

    public UserProfile getProfile(Long userId) {
        return profileRepository.findByUserId(userId)
                .orElse(UserProfile.builder().userId(userId).build());
    }

    @Transactional
    public UserProfile createOrUpdateProfile(Long userId, ProfileRequest request) {
        UserProfile profile = profileRepository.findByUserId(userId)
                .orElse(UserProfile.builder().userId(userId).build());

        if (request.getAddress() != null) profile.setAddress(request.getAddress());
        if (request.getCity() != null) profile.setCity(request.getCity());
        if (request.getState() != null) profile.setState(request.getState());
        if (request.getPincode() != null) profile.setPincode(request.getPincode());
        if (request.getProfileImageUrl() != null) profile.setProfileImageUrl(request.getProfileImageUrl());

        return profileRepository.save(profile);
    }
}
