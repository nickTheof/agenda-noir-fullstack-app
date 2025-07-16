import {type Dispatch, type SetStateAction} from "react";
import {
    Pagination,
    PaginationContent,
    PaginationItem,
    PaginationLink, PaginationNext,
    PaginationPrevious,
    PaginationEllipsis
} from "@/components/ui/pagination.tsx";

export type PaginationControlProps = {
    page: number;
    limit: number;
    totalItems: number;
    totalPages: number;
}

export type CustomPaginationProps = {
    pagination: PaginationControlProps;
    setPagination: Dispatch<SetStateAction<PaginationControlProps>>;
}

const CustomPaginationComponent = ({
    pagination,
    setPagination,
                                   }: CustomPaginationProps) => {

    const handlePageChange = (newPage: number) => {
        if (newPage >= 1 && newPage <= pagination.totalPages) {
            setPagination(prev => ({ ...prev, page: newPage }));
        }
    };


    return (
        <>
            <Pagination className="mt-8">
                <PaginationContent>
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

                    {pagination.totalPages > 2 && (
                        <PaginationItem>
                            <PaginationLink
                                href="#"
                                onClick={(e) => {
                                    e.preventDefault();
                                    handlePageChange(2);
                                }}
                                isActive={pagination.page === 2}
                            >
                                2
                            </PaginationLink>
                        </PaginationItem>
                    )}

                    {pagination.totalPages > 3 && pagination.page !== 3 && (
                        <PaginationItem>
                            <PaginationEllipsis />
                        </PaginationItem>
                    )}

                    {pagination.totalPages > 2 && (
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
        </>
    )
}

export default CustomPaginationComponent;