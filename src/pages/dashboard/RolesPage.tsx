import usePageTitle from "@/hooks/usePageTitle.tsx";
import {useEffect, useState} from "react";
import type {RoleResponse} from "@/core/types.ts";
import {useAuth} from "@/hooks/useAuth.tsx";
import {toast} from "sonner";
import {useNavigate} from "react-router";
import {deleteRole, getRoles} from "@/api/roles.ts";
import LoaderComponent from "@/components/LoaderComponent.tsx";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {Plus} from "lucide-react";
import {Button} from "@/components/ui/button";
import {Select, SelectTrigger, SelectValue, SelectContent, SelectItem} from "@/components/ui/select";
import RoleCardComponent from "@/components/RoleCardComponent.tsx";
import RoleCreateComponent from "@/components/RoleCreateComponent.tsx";
import DeleteDialogComponent from "@/components/DeleteDialogComponent.tsx";
import {ACTIONS, RESOURCES} from "@/core/constants.ts";

const RolesPage = () => {
    usePageTitle("Roles Management");
    const [roles, setRoles] = useState<RoleResponse[]>([]);
    const [selectedRoleId, setSelectedRoleId] = useState<number | null>(null);
    const [createMode, setCreateMode] = useState<boolean>(false);
    const [loading, setLoading] = useState<boolean>(true);
    const [deleteState, setDeleteState] = useState<{
        isOpen: boolean;
        roleId: number | null;
    }>({
        isOpen: false,
        roleId: null,
    });
    const {
        accessToken,
        userHasAuthority
    } = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
       if (!accessToken || !userHasAuthority("READ_ROLE")) {
           toast.error("You have not access to the requested resources");
           navigate("/", {
               replace: true,
           });
       }
       if (accessToken) {
           getRoles(accessToken).then(
               (data) => {
                   setRoles(data);
               }
           ).catch((err) => {
               toast.error(err instanceof Error ? err.message : "Fetching roles failed...");
               navigate("/", {
                   replace: true,
               });
           }).finally(
               () => setLoading(false),
           )
       }
    },[userHasAuthority, navigate, accessToken])

    const selectedRole = roles.find(role => role.id === selectedRoleId);

    const handleValueChange = (value: string) => {
        setSelectedRoleId(value ? parseInt(value) : null);
    };

    const onCreateMode = () => {
        setSelectedRoleId(null);
        setCreateMode(prev => !prev);
    }

    const handleDelete = async () => {
        if (!deleteState.roleId || !accessToken) return;
        try {
            await deleteRole(accessToken, deleteState.roleId);
            toast.success("Role deleted successfully.");
            setRoles(prev =>
                        prev.filter((role) => role.id !== deleteState.roleId)
            );
            setSelectedRoleId(null);
        }
        catch (err) {
            toast.error(err instanceof Error ? err.message : "Project deleted failed");
        } finally {
            setDeleteState({ isOpen: false, roleId: null });
        }
    };

    if (loading) {
        return <LoaderComponent
            title="Loading..."
            subtitle="Fetching application roles, stand by." />
    }
    return (
        <>
            <Card className="w-md mx-auto mb-4">
                <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                    <CardTitle className="text-2xl font-bold">Roles Management</CardTitle>
                    {userHasAuthority("CREATE_ROLE") && (
                        <Button size="sm" onClick={onCreateMode}>
                            <Plus className="mr-2 h-4 w-4" />
                            Create New Role
                        </Button>
                    )}
                </CardHeader>
                <CardContent className="space-y-6">
                    <div className="w-full max-w-md">
                        <Select
                            name="role"
                            value={selectedRoleId?.toString() ?? ""}
                            onValueChange={handleValueChange}
                            disabled={createMode}
                        >
                            <SelectTrigger className="w-full">
                                <SelectValue placeholder="Select a role..." />
                            </SelectTrigger>
                            <SelectContent>
                                {roles.map((role) => (
                                    <SelectItem key={role.id} value={role.id.toString()}>
                                        {role.name}
                                    </SelectItem>
                                ))}
                            </SelectContent>
                        </Select>
                    </div>
                </CardContent>
            </Card>
            {selectedRole && (
                <>
                    <RoleCardComponent
                        RESOURCES={RESOURCES}
                        ACTIONS={ACTIONS}
                        role={selectedRole}
                        setRoles={setRoles}
                        onDeleteClick={() => setDeleteState({isOpen: true, roleId: selectedRole?.id})}
                    />
                    <DeleteDialogComponent
                        open={deleteState.isOpen}
                        onOpenChange={(open) => setDeleteState(prev => ({
                            ...prev,
                            isOpen: open,
                        }))}
                        onDelete={handleDelete}
                        entity="role"
                    />
                </>

            )}
            {createMode && (
                <RoleCreateComponent
                    RESOURCES={RESOURCES}
                    ACTIONS={ACTIONS}
                    setCreateMode={setCreateMode}
                    setRoles={setRoles}
                />
            )}
        </>
    )
}

export default RolesPage;