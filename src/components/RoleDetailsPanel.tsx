import type {RoleResponse} from "@/core/types.ts";
import RoleComponent from "@/components/RoleComponent.tsx";

type RoleDetailsPanelProps = {
    role: RoleResponse | null;
}

export const RoleDetailsPanel = ({ role }: RoleDetailsPanelProps) => {
    if (!role) return null;

    return (
        <div className="space-y-4">
            <h3 className="text-lg font-medium">Selected Role Details</h3>
            <RoleComponent role={role} />
        </div>
    );
};