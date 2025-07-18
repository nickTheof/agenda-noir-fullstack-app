import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/components/ui/card.tsx";
import type {Action, PermissionName, Resource, RoleResponse, RoleUpdateFields} from "@/core/types.ts";
import { Checkbox } from "@/components/ui/checkbox.tsx";
import { Label } from "@/components/ui/label.tsx";
import { useEffect, useState } from "react";
import { Button } from "@/components/ui/button.tsx";
import { useForm, Controller } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Badge } from "@/components/ui/badge";
import {roleUpdateSchema} from "@/core/zod-schemas.ts";
import type {Dispatch, SetStateAction} from "react";
import {updateRole} from "@/api/roles.ts";
import {useAuth} from "@/hooks/useAuth.tsx";
import {toast} from "sonner";


type RoleCardComponentProps = {
    RESOURCES: Resource[];
    ACTIONS: Action[];
    role: RoleResponse;
    setRoles: Dispatch<SetStateAction<RoleResponse[]>>;
    onDeleteClick: () => void | Promise<void>;
}


const RoleCardComponent = (
    { RESOURCES, ACTIONS, role, setRoles, onDeleteClick }: RoleCardComponentProps
) => {
    const [edit, setEdit] = useState<boolean>(false);
    const {
        control,
        handleSubmit,
        reset,
        watch,
        formState: { isSubmitting },
    } = useForm<RoleUpdateFields>({
        resolver: zodResolver(roleUpdateSchema),
        defaultValues: {
            permissions: role.permissions.flatMap(per => per.name) || [],
        },
    });
    const {accessToken, userHasAuthority} = useAuth();

    const selectedPermissions = watch("permissions");

    useEffect(() => {
        reset({ permissions: role.permissions.flatMap(per => per.name) || [] });
        setEdit(false);
    }, [reset, role.id, role.permissions]);

    const handleSaveUpdate = (data: RoleUpdateFields) => {
        if (!accessToken) return;
        updateRole(accessToken, data, role.id, role.name)
        .then((data) => {
            setRoles(prev => (
                prev.map(roleRes => {
                        if (roleRes.id === role.id) {
                            return data
                        } else {
                            return roleRes
                        }
                }))
            );
            toast.success("Role updated successfully.");
        }).catch((err) => {
            toast.error(err instanceof Error ? err.message : "Something went wrong");
        }).finally(() => setEdit(false));
    };

    const handleCancel = () => {
        reset({ permissions: role.permissions.flatMap(per => per.name) || [] });
        setEdit(false);
    };

    return (
        <Card className="w-full max-w-3xl mx-auto">
            <CardHeader>
                <CardTitle className="text-2xl font-bold flex items-center gap-3">
                    <span>{role.name}</span>
                    {selectedPermissions.length > 0 && (
                        <Badge variant="outline" className="px-2 py-1">
                            {selectedPermissions.length} permissions selected
                        </Badge>
                    )}
                </CardTitle>
            </CardHeader>
            <CardContent>
                <form onSubmit={handleSubmit(handleSaveUpdate)} className="space-y-6">
                    <div className="space-y-3">
                        <Label className="text-sm font-medium leading-none">
                            Permissions
                        </Label>
                        <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
                            {RESOURCES.map((resource) => (
                                <div key={resource} className="space-y-3">
                                    <h3 className="font-medium text-sm border-b pb-2">
                                        {resource}
                                    </h3>
                                    <div className="space-y-2">
                                        {ACTIONS.map((action) => {
                                            const permissionId = `${action}_${resource}` as PermissionName;
                                            return (
                                                <div key={permissionId} className="flex items-center gap-2">
                                                    <Controller
                                                        control={control}
                                                        name="permissions"
                                                        render={({ field }) => (
                                                            <Checkbox
                                                                id={permissionId}
                                                                name={permissionId}
                                                                disabled={!edit}
                                                                checked={field.value.includes(permissionId)}
                                                                onCheckedChange={(checked) => {
                                                                    if (checked) {
                                                                        field.onChange([...field.value, permissionId]);
                                                                    } else {
                                                                        field.onChange(
                                                                            field.value.filter((v) => v !== permissionId)
                                                                        );
                                                                    }
                                                                }}
                                                            />
                                                        )}
                                                    />
                                                    <Label htmlFor={permissionId} className="text-sm font-normal">
                                                        {action}
                                                    </Label>
                                                </div>
                                            );
                                        })}
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                </form>
            </CardContent>
            <CardFooter className="justify-end border-t pt-4">
                {edit ? (
                    <div className="flex gap-3">
                        <Button
                            variant="outline"
                            onClick={handleCancel}
                            type="button"
                        >
                            Cancel
                        </Button>
                        <Button
                            type="submit"
                            disabled={isSubmitting}
                            onClick={handleSubmit(handleSaveUpdate)}
                        >
                            {isSubmitting ? "Saving..." : "Save Changes"}
                        </Button>
                    </div>
                ) : (
                    <div className="flex gap-3">
                        {
                            userHasAuthority("UPDATE_ROLE") && <Button
                                onClick={() => setEdit(true)}>
                            Edit Permissions
                        </Button>
                        }
                        {
                            userHasAuthority("DELETE_ROLE") && <Button
                                variant="destructive"
                                onClick={onDeleteClick}
                            >
                                Delete
                            </Button>
                        }
                    </div>
                )}
            </CardFooter>
        </Card>
    );
};

export default RoleCardComponent;