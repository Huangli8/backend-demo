package com.saygobackend.controller;

import com.saygobackend.dto.UserProfileDTO;
import com.saygobackend.dto.UserTagRequest;
import com.saygobackend.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @PostMapping("/{userId}/profile")
    public ResponseEntity<?> updateProfile(@PathVariable Long userId, @RequestBody UserProfileDTO dto) {
        userProfileService.updateProfile(userId, dto);
        return ResponseEntity.ok("用户资料已更新");
    }

    @PostMapping("/{userId}/tags")
    public ResponseEntity<?> updateUserTags(@PathVariable Long userId, @RequestBody UserTagRequest request) {
        userProfileService.updateUserTags(userId, request);
        return ResponseEntity.ok("兴趣标签已更新");
    }
}
