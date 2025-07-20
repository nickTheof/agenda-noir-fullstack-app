import { useForm, Controller } from "react-hook-form";
import { Button } from "@/components/ui/button";
import { Check, X } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import {
    Popover,
    PopoverTrigger,
    PopoverContent,
} from "@/components/ui/popover";
import {
    Command,
    CommandInput,
    CommandList,
    CommandEmpty,
    CommandItem,
} from "@/components/ui/command";
import type {RoleResponse, UserRoleEditFormValues} from "@/core/types.ts";
import {zodResolver} from "@hookform/resolvers/zod";
import {userRolesFormSchema} from "@/core/zod-schemas.ts";

type RoleSelectionFormProps = {
    allRoles: RoleResponse[];
    onSubmit: (values: UserRoleEditFormValues) => Promise<void>;
    onCancel: () => void;
    isSubmitting: boolean;
}

export const RoleSelectionForm = ({
                                      allRoles,
                                      onSubmit,
                                      onCancel,
                                      isSubmitting,
                                  }: RoleSelectionFormProps) => {
    const {
        control,
        handleSubmit,
    } = useForm<UserRoleEditFormValues>({
        defaultValues: { roleNames: [] },
        resolver: zodResolver(userRolesFormSchema),
    });

    const handleRemoveRole = (name: string, onChange: (value: string[]) => void, currentValue: string[]) => {
        onChange(currentValue.filter((val) => val !== name));
    };

    return (
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
            <Controller
                control={control}
                name="roleNames"
                render={({ field }) => (
                    <div className="space-y-2">
                        <label className="block text-sm font-medium">Add New Role(s)</label>

                        <Popover>
                            <PopoverTrigger asChild>
                                <Button
                                    variant="outline"
                                    role="combobox"
                                    className="w-full justify-between"
                                >
                                    {field.value.length > 0
                                        ? `Selected: ${field.value.length} role(s)`
                                        : "Select roles..."}
                                </Button>
                            </PopoverTrigger>
                            <PopoverContent className="w-[400px] p-0">
                                <Command>
                                    <CommandInput placeholder="Search roles..." />
                                    <CommandList>
                                        <CommandEmpty>No roles found.</CommandEmpty>
                                        {allRoles.map((role) => {
                                            const isSelected = field.value.includes(role.name);
                                            return (
                                                <CommandItem
                                                    key={role.name}
                                                    value={role.name}
                                                    onSelect={() => {
                                                        if (isSelected) {
                                                            handleRemoveRole(role.name, field.onChange, field.value);
                                                        } else {
                                                            field.onChange([...field.value, role.name]);
                                                        }
                                                    }}
                                                    className="flex justify-between items-center"
                                                >
                                                    <span>{role.name}</span>
                                                    {isSelected && (
                                                        <Check className="w-4 h-4 text-muted-foreground" />
                                                    )}
                                                </CommandItem>
                                            );
                                        })}
                                    </CommandList>
                                </Command>
                            </PopoverContent>
                        </Popover>

                        <div className="flex flex-wrap gap-2 mt-2">
                            {field.value.map((name) => {
                                const role = allRoles.find((r) => r.name === name);
                                if (!role) return null;
                                return (
                                    <Badge
                                        key={name}
                                        variant="secondary"
                                        className="flex items-center gap-1 pr-1"
                                    >
                                        {role.name}
                                        <button
                                            type="button"
                                            className="focus:outline-none"
                                            onClick={(e) => {
                                                e.preventDefault();
                                                e.stopPropagation();
                                                handleRemoveRole(name, field.onChange, field.value);
                                            }}
                                        >
                                            <X className="h-3 w-3 ml-1 hover:text-destructive" />
                                        </button>
                                    </Badge>
                                );
                            })}
                        </div>
                    </div>
                )}
            />

            <div className="flex justify-end gap-2">
                <Button
                    type="button"
                    variant="outline"
                    onClick={onCancel}
                    disabled={isSubmitting}
                >
                    Cancel
                </Button>
                <Button type="submit" disabled={isSubmitting}>
                    {isSubmitting ? "Saving..." : "Save Changes"}
                </Button>
            </div>
        </form>
    );
};