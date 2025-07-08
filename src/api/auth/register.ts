import type {RegisterFields, RegisterUserResponse} from "@/core/types";

const BASE_URL: string = import.meta.env.VITE_API_URL;

export async function registerUser(
    {username, password, firstname, lastname}: RegisterFields
): Promise<RegisterUserResponse> {
    const res = await fetch(`${BASE_URL}/auth/register/open`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            username,
            password,
            firstname,
            lastname,
        })
    });
    if (!res.ok){
        let detail = "User registration failed.";
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