import { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router';
import { toast } from 'sonner';
import type {RoleResponse} from "@/core/types.ts";
import {fetchUserRoles, patchUserRoles} from '@/api/user-roles';
import {getRoles} from "@/api/roles.ts";
import type {UserRoleEditFormValues} from "@/core/types.ts";


type userRoleEditStateProps = {
    roles: RoleResponse[];
    allRoles: RoleResponse[];
    selectedRole: RoleResponse | null;
    selectedToRemove: string[];
    isLoading: boolean;
    isEditMode: boolean;
    isSubmitting: boolean;
}

export const useUserRoleEdit = (userUuid: string, accessToken: string) => {
    const navigate = useNavigate();
    const [state, setState] = useState<userRoleEditStateProps>({
        roles: [],
        allRoles: [],
        selectedRole: null,
        selectedToRemove: [],
        isLoading: true,
        isEditMode: false,
        isSubmitting: false,
    });

    const loadData = useCallback(async () => {
        if (!userUuid || !accessToken) {
            navigate('/dashboard/projects/view');
            return;
        }

        try {
            setState(prev => ({ ...prev, isLoading: true }));

            const userRoles = await fetchUserRoles(accessToken, userUuid);
            const allAvailableRoles = await getRoles(accessToken);

            setState(prev => ({
                ...prev,
                roles: userRoles,
                allRoles: allAvailableRoles.filter(role =>
                    !userRoles.some(r => r.id === role.id)
                ),
                isLoading: false,
            }));
        } catch (error) {
            toast.error(error instanceof Error ? error.message : 'Failed to load roles');
            navigate('/dashboard/projects/view');
        }
    }, [userUuid, accessToken, navigate]);

    useEffect(() => {
        loadData().catch((err) => {
                toast.error(err instanceof Error ? err.message : "Something went wrong")
        }
        );
    }, [loadData]);

    const handleEditClick = () => {
        setState(prev => ({ ...prev, isEditMode: true }));
        toast.info('You can add or remove roles now.');
    };

    const handleCancelEdit = () => {
        setState(prev => ({
            ...prev,
            isEditMode: false,
            selectedToRemove: [],
        }));
    };

    const handleToggleRemove = (roleId: string) => {
        setState(prev => ({
            ...prev,
            selectedToRemove: prev.selectedToRemove.includes(roleId)
                ? prev.selectedToRemove.filter(id => id !== roleId)
                : [...prev.selectedToRemove, roleId],
        }));
    };

    const handleSubmit = async (formValues: UserRoleEditFormValues) => {
        if (!accessToken || !userUuid) return;
        console.log(formValues)

        try {
            setState(prev => ({ ...prev, isSubmitting: true }));

            const rolesToKeep = state.roles.filter(
                role => !state.selectedToRemove.includes(role.name)
            );

            const rolesToAdd = state.allRoles.filter(
                role => formValues.roleNames.includes(role.name)
            );

            const finalRoles = [...rolesToKeep, ...rolesToAdd];
            const roleNames = finalRoles.map(role => role.name);

            await patchUserRoles(accessToken, userUuid, {roleNames});
            toast.success('User roles updated successfully.');

            await loadData();
            setState(prev => ({
                ...prev,
                isEditMode: false,
                selectedToRemove: [],
                isSubmitting: false,
            }));
        } catch (error) {
            toast.error(error instanceof Error ? error.message : 'Failed to update roles');
            setState(prev => ({ ...prev, isSubmitting: false }));
        }
    };

    return {
        ...state,
        handleEditClick,
        handleCancelEdit,
        handleToggleRemove,
        handleSubmit,
        setSelectedRole: (role: RoleResponse | null) =>
            setState(prev => ({ ...prev, selectedRole: role })),
    };
};