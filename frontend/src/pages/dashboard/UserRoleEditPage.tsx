import { useParams } from "react-router";
import { useAuth } from "@/hooks/useAuth";
import usePageTitle from "@/hooks/usePageTitle";
import {useUserRoleEdit} from "@/hooks/useUserRoleEdit.tsx";
import LoaderComponent from "@/components/LoaderComponent.tsx";
import {UserRoleTable} from "@/components/UserRoleTable.tsx";
import {RoleDetailsPanel} from "@/components/RoleDetailsPanel.tsx";
import {RoleSelectionForm} from "@/components/RoleSelectionForm.tsx";


export const UserRoleEditPage = () => {
    usePageTitle("User Role Edit");
    const { userUuid } = useParams<{ userUuid: string }>();
    const { accessToken } = useAuth();

    const {
        roles,
        allRoles,
        selectedRole,
        selectedToRemove,
        isLoading,
        isEditMode,
        isSubmitting,
        setSelectedRole,
        handleEditClick,
        handleCancelEdit,
        handleToggleRemove,
        handleSubmit,
    } = useUserRoleEdit(userUuid || '', accessToken || '');

    if (isLoading) {
        return <LoaderComponent title="Loading..." subtitle="Fetching user roles, stand by." />;
    }

    return (
        <div className="flex flex-col gap-6 max-w-lg mx-auto my-4">
            <UserRoleTable
                roles={roles}
                selectedRole={selectedRole}
                editMode={isEditMode}
                selectedToRemove={selectedToRemove}
                onSelectRole={setSelectedRole}
                onToggleRemove={handleToggleRemove}
                onEditClick={handleEditClick}
                onCancelEdit={handleCancelEdit}
                userUuid={userUuid}
            />

            {!isEditMode && <RoleDetailsPanel role={selectedRole} />}

            {isEditMode && (
                <RoleSelectionForm
                    allRoles={allRoles}
                    onSubmit={handleSubmit}
                    onCancel={handleCancelEdit}
                    isSubmitting={isSubmitting}
                />
            )}
        </div>
    );
};

export default UserRoleEditPage;