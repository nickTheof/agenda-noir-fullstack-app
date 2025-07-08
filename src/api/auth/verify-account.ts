import type {ApiMessageResponse} from "@/core/types.ts";

const BASE_URL: string = import.meta.env.VITE_API_URL;

export async function verifyAccount(
    token: string
): Promise<ApiMessageResponse> {
    const res = await fetch(`${BASE_URL}/auth/verify-account`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            token
        })
    });
    if (!res.ok) {
        let detail = "Verification account failed.";
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