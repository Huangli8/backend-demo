package com.saygobackend.entity;

import lombok.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTagId implements Serializable {
    private Long userId;
    private Long tagId;
} //复合主键类
