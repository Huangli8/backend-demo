package com.saygobackend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_tags")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(UserTagId.class)
public class UserTag {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "tag_id")
    private Long tagId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
