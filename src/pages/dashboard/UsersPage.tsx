import { useEffect, useState } from "react";
import type { UserResponse } from "@/core/types.ts";
import CustomPaginationComponent, {
    type PaginationControlProps,
} from "@/components/CustomPaginationComponent.tsx";
import {deleteUserAccount, getUsersPaginated} from "@/api/users.ts";
import { useAuth } from "@/hooks/useAuth.tsx";
import { useNavigate } from "react-router";
import LoaderComponent from "@/components/LoaderComponent.tsx";
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from "@/components/ui/table.tsx";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card.tsx";
import { Button } from "@/components/ui/button.tsx";
import {Trash2, Pencil} from "lucide-react";
import DeleteDialogComponent from "@/components/DeleteDialogComponent.tsx";
import {toast} from "sonner";
import {Badge} from "@/components/ui/badge.tsx";
import {cn} from "@/lib/utils.ts";

const UsersPage = () => {
    const [users, setUsers] = useState<UserResponse[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [pagination, setPagination] = useState<PaginationControlProps>({
        page: 1,
        limit: 8,
        totalItems: 0,
        totalPages: 1,
    });
    const [deleteState, setDeleteState] = useState<{
        isOpen: boolean;
        userUuid: string | null;
    }>({
        isOpen: false,
        userUuid: null,
    });
    const { accessToken, userHasAuthority } = useAuth();
    const navigate = useNavigate();


    const onClickDelete = (userUuid: string) => {
        setDeleteState({
            isOpen: true,
            userUuid
        })
    }

    const handleDelete = async () => {
        if (!deleteState.userUuid || !accessToken) return;
        try {
            await deleteUserAccount(deleteState.userUuid, accessToken);
            toast.success("User deleted successfully.");
            setUsers(prevState =>
                prevState.filter(user => user.uuid != deleteState.userUuid)
            )
        }
        catch (err) {
            toast.error(err instanceof Error ? err.message : "User deleted failed");
        } finally {
            setDeleteState({ isOpen: false, userUuid: null });
        }
    };

    const onUpdateUser = (userUuid: string) => {
        navigate(`${userUuid}/edit`);
    }

    useEffect(() => {
        if (!accessToken) {
            navigate("/auth/login", { replace: true });
        } else {
            getUsersPaginated(accessToken, pagination.page, pagination.limit)
                .then((data) => {
                    setUsers(data.data);
                    setPagination((prev) => ({
                        ...prev,
                        total: data.totalItems,
                        totalPages: data.totalPages,
                    }));
                }).catch((err) => {
                    toast.error(err instanceof Error ? err.message : "Something went wrong");
                    navigate("/dashboard/projects/view", {
                        replace: true,
                    });
                }).finally(() => setLoading(false));
        }
    }, [accessToken, navigate, pagination.limit, pagination.page]);

    if (loading) {
        return <LoaderComponent title="Loading..." subtitle="Fetching user details, stand by." />;
    }

    return (
        <>
            <div className="container mx-auto py-8">
                <Card>
                    <CardHeader>
                        <div className="flex justify-between items-center">
                            <div>
                                <CardTitle>User Management</CardTitle>
                                <CardDescription>
                                    View and manage all registered users
                                </CardDescription>
                            </div>
                            {userHasAuthority("CREATE_USER") &&
                                <Button
                                    onClick={() => navigate("new") }
                                    variant="outline"
                                >Add New User
                                </Button>}

                        </div>
                    </CardHeader>
                    <CardContent>
                        <div className="rounded-md border">
                            <Table>
                                <TableHeader className="bg-gray-50 dark:bg-gray-800">
                                    <TableRow>
                                        <TableHead className="w-[200px]">UUID</TableHead>
                                        <TableHead>Email</TableHead>
                                        <TableHead>Firstname</TableHead>
                                        <TableHead>Lastname</TableHead>
                                        <TableHead className="text-center">Status</TableHead>
                                        {(userHasAuthority("DELETE_USER") || userHasAuthority("UPDATE_USER")) &&  <TableHead className="text-center">Actions</TableHead>}
                                    </TableRow>
                                </TableHeader>
                                <TableBody>
                                    {users.map((user) => (
                                        <TableRow key={user.id} className="hover:bg-gray-50 dark:hover:bg-gray-800">
                                            <TableCell className="font-medium">{user.uuid}</TableCell>
                                            <TableCell>{user.username}</TableCell>
                                            <TableCell>{user.firstname}</TableCell>
                                            <TableCell>{user.lastname}</TableCell>
                                            <TableCell>
                                                <div className="flex justify-center flex-wrap gap-2">
                                                    {user.isDeleted ? (
                                                        <Badge variant="destructive">
                                                            Deleted
                                                        </Badge>
                                                    ) : (
                                                        <>
                                                            <Badge className={cn(user.enabled
                                                                ? "bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200"
                                                                : "bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200")}>
                                                                {user.enabled ? "Active" : "Inactive"}
                                                            </Badge>
                                                            <Badge className={cn(user.verified
                                                                ? "bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200"
                                                                : "bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200")}>
                                                                {user.verified ? "Verified" : "Unverified"}
                                                            </Badge>
                                                        </>

                                                    )}
                                                </div>
                                            </TableCell>
                                            {(userHasAuthority("DELETE_USER") || userHasAuthority("UPDATE_USER")) &&
                                                <TableCell>
                                                    <div className="flex justify-center flex-wrap gap-2">
                                                        {userHasAuthority("UPDATE_USER") &&
                                                            <Button onClick={() => onUpdateUser(user.uuid)} variant="ghost" size="sm" className="h-8 w-8 p-0">
                                                                <Pencil className="h-4 w-4" />
                                                            </Button>
                                                        }
                                                        {userHasAuthority("DELETE_USER") &&
                                                            <Button onClick={() => onClickDelete(user.uuid)} variant="destructive" size="sm" className="h-8 w-8 p-0">
                                                                <Trash2 className="h-4 w-4" />
                                                            </Button>
                                                        }
                                                    </div>
                                                </TableCell>
                                            }
                                        </TableRow>
                                    ))}
                                </TableBody>
                            </Table>
                        </div>

                        {users.length > 0 && pagination.totalPages > 1 && (
                            <div className="mt-4">
                                <CustomPaginationComponent
                                    pagination={pagination}
                                    setPagination={setPagination}
                                />
                            </div>
                        )}
                    </CardContent>
                </Card>
            </div>
            <DeleteDialogComponent
                open={deleteState.isOpen}
                onOpenChange={(open) => setDeleteState(prev => ({
                    ...prev,
                    isOpen: open,
                }))}
                onDelete={handleDelete}
                entity="user"
            />
        </>
    );
};

export default UsersPage;