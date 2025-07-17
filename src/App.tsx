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
import ProtectedRoute from "@/components/ProtectedRoute.tsx";
import ProjectsPage from "@/pages/dashboard/ProjectsPage.tsx";
import DashboardLayout from "@/components/DashboardLayout.tsx";
import ProjectCreatePage from "@/pages/dashboard/ProjectCreatePage.tsx";
import SettingsPage from "@/pages/dashboard/SettingsPage.tsx";
import TicketsPage from "@/pages/dashboard/TicketsPage.tsx";
import TicketModePage from "@/pages/dashboard/TicketModePage.tsx";
import NotFoundPage from "@/pages/dashboard/NotFoundPage.tsx";

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
                        <Route element={<DashboardLayout/>}>
                            <Route path="dashboard" element={<ProtectedRoute />}>
                                <Route path="projects">
                                    <Route path="view" element={<ProjectsPage />}></Route>
                                    <Route path="new" element={<ProjectCreatePage />}></Route>
                                    <Route path=":projectUuid">
                                        <Route path="tickets">
                                            <Route index element={<TicketsPage/>} />
                                            <Route path="new" element={<TicketModePage mode="create" />} />
                                            <Route path=":ticketUuid/update" element={<TicketModePage mode="edit" />}></Route>
                                        </Route>
                                    </Route>
                                </Route>

                                <Route path="profile">
                                    <Route path="settings" element={<SettingsPage />}></Route>
                                </Route>
                            </Route>
                        </Route>
                        <Route element={<Layout />}>
                            <Route path="*" element={<NotFoundPage />} />
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
