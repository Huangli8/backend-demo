package com.saygobackend.service.impl;

import com.saygobackend.dto.UserProfileDTO;
import com.saygobackend.dto.UserTagRequest;
import com.saygobackend.entity.UserProfile;
import com.saygobackend.entity.UserTag;
import com.saygobackend.repository.UserProfileRepository;
import com.saygobackend.repository.UserTagRepository;
import com.saygobackend.service.UserProfileService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserTagRepository userTagRepository;

    @Override
    @Transactional
    public void updateProfile(Long userId, UserProfileDTO dto) {
        UserProfile profile = UserProfile.builder()
                .id(userId)
                .fullName(dto.getFullName())
                .gender(dto.getGender())
                .age(dto.getAge())
                .familySize(dto.getFamilySize())
                .build();
        userProfileRepository.save(profile);
    }

    @Override
    @Transactional
    public void updateUserTags(Long userId, UserTagRequest request) {
        // 先清空旧的兴趣标签
        userTagRepository.deleteAll(
                userTagRepository.findAll().stream()
                        .filter(ut -> ut.getUserId().equals(userId))
                        .collect(Collectors.toList())
        );

        // 保存新的
        request.getTagIds().forEach(tagId -> {
            UserTag ut = UserTag.builder()
                    .userId(userId)
                    .tagId(tagId)
                    .createdAt(LocalDateTime.now())
                    .build();
            userTagRepository.save(ut);
        });
    }
}
