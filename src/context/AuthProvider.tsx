import { AuthContext } from "@/context/AuthContext.ts";
import { useState, useEffect, type ReactNode, useCallback, useMemo } from "react";
import { jwtDecode } from "jwt-decode";
import { setCookie, deleteCookie, getCookie } from "@/utils/cookie.ts";
import { login } from "@/api/auth.ts";
import type { LoginFields, JwtPayload } from "@/core/types.ts";
import { fetchUserRoles } from "@/api/user-roles.ts";
import LoaderComponent from "@/components/LoaderComponent.tsx";

const isProduction = import.meta.env.VITE_NODE_ENV === "production";
const COOKIE_NAME = "access_token";
const TOKEN_EXPIRY_HOURS = 3;

export const AuthProvider = ({ children }: { children: ReactNode }) => {
    const [accessToken, setAccessToken] = useState<string | null>(null);
    const [userAuthorities, setUserAuthorities] = useState<string[]>([]);
    const [loading, setLoading] = useState<boolean>(true);

    const decodedToken = useMemo(() => {
        if (!accessToken) return null;
        try {
            return jwtDecode<JwtPayload>(accessToken);
        } catch {
            return null;
        }
    }, [accessToken]);

    const userUuid = useMemo(() => decodedToken?.userUuid ?? null, [decodedToken]);
    const username = useMemo(() => decodedToken?.sub ?? null, [decodedToken]);

    const fetchUserAuthorities = useCallback(async (token: string, uuid: string) => {
        try {
            const roles = await fetchUserRoles(token, uuid);
            const authorities = roles.flatMap(role =>
                role.permissions.map(permission => permission.name)
            );
            setUserAuthorities([...new Set(authorities)]);
        } catch (err) {
            console.error("Failed to fetch user authorities:", err);
            setUserAuthorities([]);
        }
    }, []);

    const logoutUser = useCallback(() => {
        deleteCookie(COOKIE_NAME, { path: "/" });
        setAccessToken(null);
        setUserAuthorities([]);
    }, []);

    const handleToken = useCallback(async (token: string | null) => {
        setAccessToken(token);
        if (!token) {
            setUserAuthorities([]);
            return;
        }
        try {
            const decoded = jwtDecode<JwtPayload>(token);
            if (decoded.exp && Date.now() / 1000 > decoded.exp) {
                console.warn("Token has expired.");
                logoutUser();
                return;
            }
            if (decoded.userUuid) {
                await fetchUserAuthorities(token, decoded.userUuid);
            }
        } catch (err) {
            console.error("Token decode or authority fetch failed:", err);
            setUserAuthorities([]);
        }
    }, [fetchUserAuthorities, logoutUser]);

    useEffect(() => {
        const token = getCookie(COOKIE_NAME);
        let isActive = true;

        handleToken(token ?? null)
            .then(() => {
                if (isActive) {
                    setLoading(false);
                }
            })
            .catch((error) => {
                if (isActive) {
                    console.error("Auth initialization failed:", error);
                    setLoading(false);
                }
            });

        return () => {
            isActive = false;
        };
    }, [handleToken]);

    const loginUser = async (fields: LoginFields) => {
        setLoading(true);
        try {
            const res = await login(fields);
            setCookie(COOKIE_NAME, res.token, {
                expires: TOKEN_EXPIRY_HOURS / 24,
                sameSite: isProduction ? "strict" : "lax",
                secure: isProduction,
                path: "/"
            });
            await handleToken(res.token);
        } catch (err) {
            console.error("Login failed:", err);
            throw err;
        } finally {
            setLoading(false);
        }
    };

    const userHasAuthority = useCallback(
        (authority: string) => userAuthorities.includes(authority),
        [userAuthorities]
    );

    return (
        <AuthContext.Provider
            value={{
                isAuthenticated: !!accessToken,
                accessToken,
                userUuid,
                username,
                loginUser,
                logout: logoutUser,
                loading,
                userAuthorities,
                userHasAuthority
            }}
        >
            {children}
            {loading && (
                <LoaderComponent
                    title="Authenticating..."
                    subtitle="Verifying credentials, stand by."
                />
            )}
        </AuthContext.Provider>
    );
};