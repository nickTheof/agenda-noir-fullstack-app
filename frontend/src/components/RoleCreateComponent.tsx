import type {Action, PermissionName, Resource, RoleFormFields, RoleResponse} from "@/core/types.ts";
import { Controller, useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/components/ui/card.tsx";
import { Checkbox } from "@/components/ui/checkbox.tsx";
import { Label } from "@/components/ui/label.tsx";
import { Button } from "@/components/ui/button.tsx";
import { Input } from "@/components/ui/input.tsx";
import type { Dispatch, SetStateAction } from "react";
import { Badge } from "@/components/ui/badge";
import {useAuth} from "@/hooks/useAuth.tsx";
import {createRole} from "@/api/roles.ts";
import {toast} from "sonner";
import {roleSchema} from "@/core/zod-schemas.ts";

type RoleCreateProps = {
    RESOURCES: Resource[];
    ACTIONS: Action[];
    setCreateMode: Dispatch<SetStateAction<boolean>>;
    setRoles: Dispatch<SetStateAction<RoleResponse[]>>;
};

const RoleCreateComponent = (
    { RESOURCES, ACTIONS, setCreateMode, setRoles }
    : RoleCreateProps) => {
    const {
        register,
        control,
        handleSubmit,
        formState: { errors, isSubmitting },
        watch,
    } = useForm<RoleFormFields>({
        resolver: zodResolver(roleSchema),
        defaultValues: {
            name: "",
            permissions: [],
        },
    });
    const {accessToken} = useAuth();

    const selectedPermissions = watch("permissions");

    const onSubmit = (data: RoleFormFields) => {
        if (!accessToken) return;
        createRole(accessToken, data)
            .then((data) => {
                setRoles(prev => [...prev, data]);
                toast.success("Role created successfully.");
                setCreateMode(false);
            }).catch((err) => {
                toast.error(err instanceof Error ? err.message : "Create error failed");
        })
    };

    const onCancel = () => {
        setCreateMode(false);
    };

    return (
        <Card className="w-full max-w-3xl mx-auto">
            <CardHeader>
                <CardTitle className="text-2xl font-bold flex items-center gap-3">
                    <span>Create New Role</span>
                    {selectedPermissions.length > 0 && (
                        <Badge variant="outline" className="px-2 py-1">
                            {selectedPermissions.length} permissions selected
                        </Badge>
                    )}
                </CardTitle>
            </CardHeader>
            <CardContent>
                <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
                    <div className="grid gap-4">
                        <div className="space-y-2">
                            <Label htmlFor="name" className="text-sm font-medium leading-none">
                                Role Name
                            </Label>
                            <Input
                                id="name"
                                type="text"
                                placeholder="e.g., Project Manager, Admin, etc."
                                autoFocus
                                {...register("name")}
                                disabled={isSubmitting}
                                autoComplete="off"
                                className="text-sm"
                            />
                            {errors.name && (
                                <p className="text-sm text-red-500">{errors.name.message}</p>
                            )}
                        </div>

                        <div className="space-y-3">
                            <Label className="text-sm font-medium leading-none">
                                Permissions
                            </Label>
                            {errors.permissions && (
                                <p className="text-sm text-red-500">{errors.permissions.message}</p>
                            )}

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
                    </div>
                </form>
            </CardContent>
            <CardFooter className="justify-end border-t pt-4">
                <div className="flex gap-3">
                    <Button
                        variant="outline"
                        onClick={onCancel}
                        disabled={isSubmitting}
                        type="button"
                    >
                        Cancel
                    </Button>
                    <Button
                        type="submit"
                        onClick={handleSubmit(onSubmit)}
                        disabled={isSubmitting}
                    >
                        {isSubmitting ? "Creating..." : "Create Role"}
                    </Button>
                </div>
            </CardFooter>
        </Card>
    );
};

export default RoleCreateComponent;