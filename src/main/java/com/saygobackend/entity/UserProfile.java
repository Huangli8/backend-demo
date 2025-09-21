package com.saygobackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {
    @Id
    private Long id; // 与 User 的 id 一致

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "gender")
    private String gender;

    @Column(name = "age")
    private Integer age;

    @Column(name = "family_size")
    private Integer familySize;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private User user;
}
