import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Button } from "@/components/ui/button";
import { Pencil } from "lucide-react";
import type {RoleResponse} from "@/core/types.ts";

type UserRoleTableProps = {
    roles: RoleResponse[];
    selectedRole: RoleResponse | null;
    editMode: boolean;
    selectedToRemove: string[];
    onSelectRole: (role: RoleResponse) => void;
    onToggleRemove: (roleId: string) => void;
    onEditClick: () => void;
    onCancelEdit: () => void;
    userUuid?: string;
}

export const UserRoleTable = ({
                                  roles,
                                  selectedRole,
                                  editMode,
                                  selectedToRemove,
                                  onSelectRole,
                                  onToggleRemove,
                                  onEditClick,
                                  onCancelEdit,
                                  userUuid,
                              }: UserRoleTableProps) => (
    <div className="border rounded-lg overflow-hidden">
        <div className="flex items-center justify-between p-4 bg-gray-50 dark:bg-gray-800">
            <div>
                <h2 className="text-xl font-semibold">User Custom Roles</h2>
                {userUuid && (
                    <p className="text-xs text-muted-foreground mt-1">
                        User ID: <span className="font-mono">{userUuid}</span>
                    </p>
                )}
            </div>
            {!editMode ? (
                <Button onClick={onEditClick} variant="outline" className="gap-2">
                    <Pencil className="h-4 w-4" />
                    Edit User Custom Roles
                </Button>
            ) : (
                <Button variant="ghost" onClick={onCancelEdit}>
                    Cancel
                </Button>
            )}
        </div>

        <Table>
            <TableHeader className="bg-gray-50 dark:bg-gray-800">
                <TableRow>
                    <TableHead className="w-[200px]">Role Name</TableHead>
                    <TableHead>Permissions Count</TableHead>
                    <TableHead className="text-right">Actions</TableHead>
                </TableRow>
            </TableHeader>

            <TableBody>
                {roles.length > 0 ? (
                    roles.map((role) => (
                        <TableRow
                            key={role.id}
                            className={`hover:bg-gray-50 dark:hover:bg-gray-800 ${
                                selectedRole?.id === role.id
                                    ? "bg-gray-100 dark:bg-gray-700"
                                    : ""
                            } ${
                                editMode && selectedToRemove.includes(role.name)
                                    ? "line-through opacity-50"
                                    : ""
                            }`}
                            onClick={() => !editMode && onSelectRole(role)}
                        >
                            <TableCell className="font-medium">{role.name}</TableCell>
                            <TableCell>{role.permissions.length}</TableCell>
                            <TableCell className="text-right">
                                {editMode ? (
                                    <Button
                                        variant={
                                            selectedToRemove.includes(role.name) ? "destructive" : "outline"
                                        }
                                        size="sm"
                                        onClick={(e) => {
                                            e.stopPropagation();
                                            onToggleRemove(role.name);
                                        }}
                                    >
                                        {selectedToRemove.includes(role.name) ? "Undo Remove" : "Remove"}
                                    </Button>
                                ) : (
                                    <Button
                                        variant="ghost"
                                        size="sm"
                                        onClick={(e) => {
                                            e.stopPropagation();
                                            onSelectRole(role);
                                        }}
                                    >
                                        View
                                    </Button>
                                )}
                            </TableCell>
                        </TableRow>
                    ))
                ) : (
                    <TableRow>
                        <TableCell colSpan={3} className="text-center h-24">
                            No custom roles found
                        </TableCell>
                    </TableRow>
                )}
            </TableBody>
        </Table>
    </div>
);