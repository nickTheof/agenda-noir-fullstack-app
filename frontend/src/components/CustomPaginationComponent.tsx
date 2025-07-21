import { type Dispatch, type SetStateAction } from "react";
import {
    Pagination,
    PaginationContent,
    PaginationItem,
    PaginationLink,
    PaginationNext,
    PaginationPrevious,
    PaginationEllipsis
} from "@/components/ui/pagination.tsx";

export type PaginationControlProps = {
    page: number;
    limit: number;
    totalItems: number;
    totalPages: number;
};

export type CustomPaginationProps = {
    pagination: PaginationControlProps;
    setPagination: Dispatch<SetStateAction<PaginationControlProps>>;
};

const CustomPaginationComponent = ({
                                       pagination,
                                       setPagination,
                                   }: CustomPaginationProps) => {
    const handlePageChange = (newPage: number) => {
        if (newPage >= 1 && newPage <= pagination.totalPages) {
            setPagination(prev => ({ ...prev, page: newPage }));
        }
    };

    const renderMiddlePages = () => {
        const { page, totalPages } = pagination;

        // For exactly 3 pages, show all pages
        if (totalPages === 3) {
            return (
                <PaginationItem>
                    <PaginationLink
                        href="#"
                        onClick={(e) => {
                            e.preventDefault();
                            handlePageChange(2);
                        }}
                        isActive={page === 2}
                    >
                        2
                    </PaginationLink>
                </PaginationItem>
            );
        }

        // For 2 pages or less, no middle section needed
        if (totalPages <= 2) return null;

        // When current page is close to start
        if (page <= 2) {
            return (
                <>
                    {page === 2 && (
                        <PaginationItem>
                            <PaginationLink
                                href="#"
                                onClick={(e) => {
                                    e.preventDefault();
                                    handlePageChange(2);
                                }}
                                isActive
                            >
                                2
                            </PaginationLink>
                        </PaginationItem>
                    )}
                    {totalPages > 3 && (
                        <PaginationItem>
                            <PaginationEllipsis />
                        </PaginationItem>
                    )}
                </>
            );
        }

        // When current page is close to end
        if (page >= totalPages - 1) {
            return (
                <>
                    <PaginationItem>
                        <PaginationEllipsis />
                    </PaginationItem>
                    <PaginationItem>
                        <PaginationLink
                            href="#"
                            onClick={(e) => {
                                e.preventDefault();
                                handlePageChange(totalPages - 1);
                            }}
                            isActive={page === totalPages - 1}
                        >
                            {totalPages - 1}
                        </PaginationLink>
                    </PaginationItem>
                </>
            );
        }

        // When current page is in the middle
        return (
            <>
                <PaginationItem>
                    <PaginationEllipsis />
                </PaginationItem>
                <PaginationItem>
                    <PaginationLink
                        href="#"
                        onClick={(e) => {
                            e.preventDefault();
                            handlePageChange(page);
                        }}
                        isActive
                    >
                        {page}
                    </PaginationLink>
                </PaginationItem>
                <PaginationItem>
                    <PaginationEllipsis />
                </PaginationItem>
            </>
        );
    };

    return (
        <Pagination className="mt-8">
            <PaginationContent>
                {/* Previous Button */}
                <PaginationItem>
                    <PaginationPrevious
                        href="#"
                        onClick={(e) => {
                            e.preventDefault();
                            handlePageChange(pagination.page - 1);
                        }}
                        isActive={pagination.page > 1}
                    />
                </PaginationItem>

                {/* Always show first page */}
                <PaginationItem>
                    <PaginationLink
                        href="#"
                        onClick={(e) => {
                            e.preventDefault();
                            handlePageChange(1);
                        }}
                        isActive={pagination.page === 1}
                    >
                        1
                    </PaginationLink>
                </PaginationItem>

                {/* Dynamic middle section */}
                {renderMiddlePages()}

                {/* Show last page if different from first */}
                {pagination.totalPages > 1 && (
                    <PaginationItem>
                        <PaginationLink
                            href="#"
                            onClick={(e) => {
                                e.preventDefault();
                                handlePageChange(pagination.totalPages);
                            }}
                            isActive={pagination.page === pagination.totalPages}
                        >
                            {pagination.totalPages}
                        </PaginationLink>
                    </PaginationItem>
                )}

                {/* Next Button */}
                <PaginationItem>
                    <PaginationNext
                        href="#"
                        onClick={(e) => {
                            e.preventDefault();
                            handlePageChange(pagination.page + 1);
                        }}
                        isActive={pagination.page < pagination.totalPages}
                    />
                </PaginationItem>
            </PaginationContent>
        </Pagination>
    );
};

export default CustomPaginationComponent;