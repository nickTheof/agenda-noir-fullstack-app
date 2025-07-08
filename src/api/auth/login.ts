import type {LoginFields, LoginResponse} from "@/core/types.ts";

const BASE_URL: string = import.meta.env.VITE_API_URL;

export async function login(
    {username, password}: LoginFields
): Promise<LoginResponse> {
    const res = await fetch(`${BASE_URL}/auth/login/access-token`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            username,
            password,
        }),
    });

    if (!res.ok){
        let detail = "Login failed.";
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