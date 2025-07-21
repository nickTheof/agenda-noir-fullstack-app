import {useEffect, useState} from "react";
import {type ThemeProviderProps, type Theme, ThemeProviderContext} from "@/context/ThemeContext.ts";

export function ThemeProvider({
                                  children,
                                  defaultTheme = "dark",
                                  storageKey = "vite-ui-theme",
                              }: ThemeProviderProps) {
    const [theme, setTheme] = useState<Theme>(
        () => (localStorage.getItem(storageKey) as Theme) || defaultTheme
    );

    useEffect(() => {
        const root = window.document.documentElement;
        root.classList.remove("light", "dark")
        root.classList.add(theme)
    }, [theme])

    const value = {
        theme,
        setTheme: (theme: Theme) => {
            localStorage.setItem(storageKey, theme)
            setTheme(theme)
        },
    }

    return (
        <ThemeProviderContext.Provider value={value}>
        {children}
        </ThemeProviderContext.Provider>
)
}