package com.example.stagemate.global.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record PagedResponse<T>(
        List<T> list,
        int currentPage,
        int pageSize,
        long totalElements,
        int totalPages
) {
    public static <T, E> PagedResponse<T> from(List<T> list, Page<E> page) {
        return new PagedResponse<>(
                list,
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}

