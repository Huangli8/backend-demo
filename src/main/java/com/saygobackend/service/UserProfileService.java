package com.saygobackend.service;

import com.saygobackend.dto.UserProfileDTO;
import com.saygobackend.dto.UserTagRequest;

public interface UserProfileService {
    void updateProfile(Long userId, UserProfileDTO dto);
    void updateUserTags(Long userId, UserTagRequest request);
}
