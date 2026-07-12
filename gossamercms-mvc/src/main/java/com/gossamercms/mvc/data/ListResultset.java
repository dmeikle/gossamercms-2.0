package com.gossamercms.mvc.data;

import java.util.List;

public record ListResultset<T>(
        List<T> list,
        int page,
        int pageSize,
        long totalCount,
        int totalPages
) {

    public static <T> ListResultset<T> of(
            List<T> list,
            int page,
            int pageSize,
            long totalCount
    ) {
        if (pageSize == -1) {
            // Return everything on one page
            return new ListResultset<>(
                    list,
                    page,
                    list.size(),
                    list.size(),
                    1
            );
        }

        int totalPages = (int) Math.ceil((double) totalCount / pageSize);

        return new ListResultset<>(
                list,
                page,
                pageSize,
                totalCount,
                totalPages
        );
    }
}