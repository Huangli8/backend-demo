package com.saygobackend.dto;

import lombok.Data;

@Data
public class UserProfileDTO {
    private String fullName;
    private String gender;
    private Integer age;
    private Integer familySize;
}

