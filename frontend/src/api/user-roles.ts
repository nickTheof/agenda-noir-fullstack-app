import type {UserRoleEditFormValues, UserRoleResponse} from "@/core/types.ts";

const BASE_URL: string = import.meta.env.VITE_API_URL;

export async function fetchUserRoles(token: string, userUuid: string): Promise<UserRoleResponse> {
    const res = await fetch(`${BASE_URL}/users/${userUuid}/roles`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
        }
    });
    if (!res.ok) {
        let detail = "Fetch user roles failed";
        try {
            const data = await res.json();
            if (typeof data?.message =="string") detail = data?.message;
        } catch (err) {
            console.error(err);
        }
        throw new Error(detail);
    }
    return await res.json();
}

export async function patchUserRoles(token: string, userUuid: string, data: UserRoleEditFormValues): Promise<UserRoleResponse> {
    const res = await fetch(`${BASE_URL}/users/${userUuid}/roles`, {
        method: 'PATCH',
        headers: {
            'Authorization': `Bearer ${token}`,
            "Content-Type": "application/json",
        },
        body: JSON.stringify(data),
    });
    if (!res.ok) {
        let detail = "Update user roles failed";
        try {
            const data = await res.json();
            if (typeof data?.message =="string") detail = data?.message;
        } catch (err) {
            console.error(err);
        }
        throw new Error(detail);
    }
    return await res.json();
}