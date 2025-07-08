import type {ApiMessageResponse, PasswordResetFields} from "@/core/types.ts";

const BASE_URL: string = import.meta.env.VITE_API_URL;

export async function sendPasswordRecoveryRequest(
    username: string
): Promise<ApiMessageResponse> {
    const res = await fetch(`${BASE_URL}/auth/password-recovery/${username}`, {
        method: "POST",
    });
    if (!res.ok) {
        let detail = "Password recovery request failed.";
        try {
            const data = await res.json();
            if (typeof data?.message =="string") detail = data?.message;
        } catch (error) {
            console.error(error);
        }
        throw new Error(detail);
    }
    return await res.json();
}

export async function sendPasswordReset(
    token: string, {newPassword}: PasswordResetFields
): Promise<ApiMessageResponse> {
    const res = await fetch(`${BASE_URL}/auth/reset-password`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            token,
            newPassword,
        }),
    });
    if (!res.ok) {
        let detail = "Password reset after recovery failed.";
        try {
            const data = await res.json();
            if (typeof data?.message =="string") detail = data?.message;
        } catch (error) {
            console.error(error);
        }
        throw new Error(detail);
    }
    return await res.json();
}
