import {createContext} from "react";
import type {LoginFields} from "@/core/types.ts";

export type AuthContextProps = {
    isAuthenticated: boolean;
    accessToken: string | null;
    userUuid: string | null;
    userAuthorities: string[];
    userHasAuthority: (authority: string) => boolean;
    loginUser: (fields: LoginFields) => Promise<void>;
    logout: () => void;
    loading: boolean;
}

export const AuthContext = createContext<AuthContextProps | undefined>(undefined);