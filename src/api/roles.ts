import type {RoleResponse, RoleUpdateFields} from "@/core/types.ts";
import type {RoleFormFields} from "@/core/types.ts";

const BASE_URL: string = import.meta.env.VITE_API_URL;


export async function getRoles(token: string): Promise<RoleResponse[]> {
    const res = await fetch(`${BASE_URL}/roles`, {
        method: "GET",
        headers: {
            Authorization: `Bearer ${token}`,
        }
    });
    if (!res.ok) {
        let detail = "Fetch roles failed";
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

export async function createRole(token: string, data: RoleFormFields): Promise<RoleResponse> {
    const res = await fetch(`${BASE_URL}/roles`, {
        method: "POST",
        headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
        },
        body: JSON.stringify(data),
    });
    if (!res.ok) {
        let detail = "Create new role failed";
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

export async function deleteRole(token: string, roleId: number): Promise<void> {
    const res = await fetch(`${BASE_URL}/roles/${roleId}`, {
        method: "DELETE",
        headers: {
            Authorization: `Bearer ${token}`,
        }
    });
    if (!res.ok) {
        let detail = "Delete role failed";
        try {
            const data = await res.json();
            if (typeof data?.message =="string") detail = data?.message;
        } catch (err) {
            console.error(err);
        }
        throw new Error(detail);
    }
}

export async function updateRole(token: string, data: RoleUpdateFields, roleId: number, roleName: string): Promise<RoleResponse> {
    const res = await fetch(`${BASE_URL}/roles/${roleId}`, {
        method: "PUT",
        headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            name: roleName,
            permissions: data.permissions,
        }),
    });
    if (!res.ok) {
        let detail = "Update role failed";
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