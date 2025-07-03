package gr.aueb.cf.projectmanagementapp.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record Paginated<T> (
        List<T> data,
        long totalItems,
        int totalPages,
        int numberOfElements,
        int currentPage,
        int pageSize
){
    public Paginated(Page<T> page) {
        this(page.getContent(), page.getTotalElements(), page.getTotalPages(), page.getNumber(), page.getPageable().getPageNumber(), page.getNumberOfElements());
    }
}

