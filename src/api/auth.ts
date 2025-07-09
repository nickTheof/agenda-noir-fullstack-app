import type {
    ApiMessageResponse,
    LoginFields,
    LoginResponse,
    PasswordResetFields,
    RegisterFields, RegisterUserResponse
} from "@/core/types.ts";

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