import {useContext} from "react";
import {ThemeProviderContext} from "@/context/ThemeContext.ts";

export const useTheme = () => {
    const ctx = useContext(ThemeProviderContext);
    if (!ctx) throw new Error("useTheme must be used within a ThemeProvider");
    return ctx;
}