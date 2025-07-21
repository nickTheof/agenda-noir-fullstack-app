package gr.aueb.cf.projectmanagementapp.core.filters;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Getter
@Setter
public abstract class GenericFilters {
    private static final int DEFAULT_PAGE_SIZE = 15;
    private static final String DEFAULT_SORT_FIELD = "id";
    private static final Sort.Direction DEFAULT_SORT_DIRECTION = Sort.Direction.ASC;

    private int page;
    private int size;
    private String sortBy;
    private Sort.Direction orderBy;

    public int getPageSize() {
        return size <= 0 ? DEFAULT_PAGE_SIZE : size;
    }

    public int getPage() {
        return Math.max(page, 0);
    }

    public Sort.Direction getSortDirection() {
        return orderBy == null ? DEFAULT_SORT_DIRECTION : orderBy;
    }

    public String getSortField() {
        if (sortBy == null || StringUtils.isBlank(sortBy)) return DEFAULT_SORT_FIELD;
        return sortBy;
    }

    public Pageable getPageable() {
        return PageRequest.of(getPage(), getPageSize(), getSort());
    }

    public Sort getSort() {
        return Sort.by(getSortDirection(), getSortField());
    }
}
