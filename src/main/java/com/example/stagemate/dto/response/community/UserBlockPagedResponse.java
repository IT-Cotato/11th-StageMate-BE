package com.example.stagemate.dto.response.community;

import com.example.stagemate.domain.community.UserBlock;
import org.springframework.data.domain.Page;

import java.util.List;

public record UserBlockPagedResponse(
        List<UserBlockListResponse> list,
        int currentPage,
        int pageSize,
        long totalElements,
        int totalPages
) {
    public static UserBlockPagedResponse from(List<UserBlockListResponse> list, Page<UserBlock> page) {
        return new UserBlockPagedResponse(
                list,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}