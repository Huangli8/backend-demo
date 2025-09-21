package com.saygobackend.repository;

import com.saygobackend.entity.UserTag;
import com.saygobackend.entity.UserTagId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface UserTagRepository extends JpaRepository<UserTag, UserTagId> {

    @Query("SELECT t.name FROM Tag t JOIN UserTag ut ON t.id = ut.tagId WHERE ut.userId = :userId")
    List<String> findTagNamesByUserId(Long userId);
}
