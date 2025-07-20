import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {Badge} from "@/components/ui/badge.tsx";
import {Label} from "@/components/ui/label.tsx";
import type { PermissionName, RoleResponse} from "@/core/types.ts";
import {Checkbox} from "@/components/ui/checkbox.tsx";
import {RESOURCES, ACTIONS} from "@/core/constants.ts";

type RoleComponentProps = {
    role: RoleResponse;
}

const RoleComponent = (
    {role}: RoleComponentProps
) => {
    const roleAuthorities = role.permissions.flatMap((role) => role.name);

    return (
        <>
            <Card className="w-full max-w-3xl mx-auto">
                <CardHeader>
                    <CardTitle className="text-2xl font-bold flex items-center gap-3">
                        <span>{role.name}</span>
                        {role.permissions.length > 0 && (
                            <Badge variant="outline" className="px-2 py-1">
                                {role.permissions.length || 0 } permissions
                            </Badge>
                        )}
                    </CardTitle>
                </CardHeader>
                <CardContent className="space-y-3">
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
                                                <Checkbox
                                                    id={permissionId}
                                                    name={permissionId}
                                                    disabled={true}
                                                    checked={roleAuthorities.includes(permissionId)}
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
                </CardContent>
            </Card>
        </>
    )
}

export default RoleComponent;