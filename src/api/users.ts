import type {ChangePasswordFields, RegisterUserResponse} from "@/core/types.ts";

const BASE_URL: string = import.meta.env.VITE_API_URL;

export async function getUserProfile(
    token: string, userUuid: string,
): Promise<RegisterUserResponse> {
    const res = await fetch(`${BASE_URL}/users/${userUuid}`, {
        method: 'GET',
        headers: {
            Authorization: `Bearer ${token}`,
        }
    });
    if (!res.ok) {
        let detail = "Fetch user role profile failed";
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

export async function changeUserPassword(
    {
        oldPassword, newPassword
    }: ChangePasswordFields,
    accessToken: string
): Promise<void> {
    const res = await fetch(`${BASE_URL}/users/me/change-password`, {
        method: 'PATCH',
        headers: {
            Authorization: `Bearer ${accessToken}`,
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            oldPassword,
            newPassword,
        }),
    });
    if (!res.ok) {
        let detail = "Change user password failed";
        try {
            const data = await res.json();
            if (typeof data?.message =="string") detail = data?.message;
        } catch (err) {
            console.error(err);
        }
        throw new Error(detail);
    }
}

export async function deleteUserAccount(
    userUuid: string,
    accessToken: string
): Promise<void> {
    const res = await fetch(`${BASE_URL}/users/${userUuid}`, {
        method: 'DELETE',
        headers: {
            Authorization: `Bearer ${accessToken}`,
            "Content-Type": "application/json",
        }
    });
    if (!res.ok) {
        let detail = "Delete user password failed";
        try {
            const data = await res.json();
            if (typeof data?.message =="string") detail = data?.message;
        } catch (err) {
            console.error(err);
        }
        throw new Error(detail);
    }
}


export async function deactivateUserAccount(
    userUuid: string,
    accessToken: string
): Promise<void> {
    const res = await fetch(`${BASE_URL}/users/${userUuid}`, {
        method: 'PATCH',
        headers: {
            Authorization: `Bearer ${accessToken}`,
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            deleted: true
        })
    });
    if (!res.ok) {
        let detail = "User's account deactivation failed";
        try {
            const data = await res.json();
            if (typeof data?.message =="string") detail = data?.message;
        } catch (err) {
            console.error(err);
        }
        throw new Error(detail);
    }
}