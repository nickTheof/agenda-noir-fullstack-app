import {BrowserRouter, Routes, Route} from "react-router";
import {Toaster} from "sonner";
import {ThemeProvider} from "@/context/ThemeProvider.tsx";
import {AuthProvider} from "@/context/AuthProvider.tsx";
import Layout from "@/components/Layout.tsx";
import LandingPage from "@/pages/LandingPage.tsx";
import LoginPage from "@/pages/auth/LoginPage.tsx";
import VerifyAccountPage from "@/pages/auth/VerifyAccountPage.tsx";
import RegisterUserPage from "@/pages/auth/RegisterUserPage.tsx";
import PasswordRecoveryPage from "@/pages/auth/PasswordRecoveryPage.tsx";
import PasswordResetPage from "@/pages/auth/PasswordResetPage.tsx";

function App() {

  return (
    <>
        <ThemeProvider defaultTheme="dark" storageKey="vite-ui-theme">
            <AuthProvider>
                <BrowserRouter>
                    <Routes>
                        <Route element={<Layout />}>
                            <Route index element={<LandingPage />} />
                            <Route path="auth">
                                <Route path="login" element={<LoginPage />} />
                                <Route path="register" element={<RegisterUserPage />} />
                                <Route path="verify-account" element={<VerifyAccountPage />} />
                                <Route path="password-recovery" element={<PasswordRecoveryPage />} />
                                <Route path="reset-password" element={<PasswordResetPage />} />
                            </Route>
                        </Route>
                    </Routes>
                </BrowserRouter>
                <Toaster richColors />
            </AuthProvider>
        </ThemeProvider>
    </>
  )
}

export default App
