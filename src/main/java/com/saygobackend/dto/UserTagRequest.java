package com.saygobackend.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserTagRequest {
    private List<Long> tagIds;
}
