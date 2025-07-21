import { Outlet } from "react-router";
import { getCookie } from "@/utils/cookie.ts";
import Header from "@/components/Header.tsx";
import Footer from "@/components/Footer.tsx";
import { SidebarProvider, SidebarTrigger } from "@/components/ui/sidebar.tsx";
import AppSideBar from "@/components/AppSideBar.tsx";

const DashboardLayout = () => {
    const openCookie = getCookie("sidebar_state") ?? "true"
    const defaultOpen = openCookie === "true"
    return (
        <>
            <Header />
            <SidebarProvider defaultOpen={defaultOpen}>
                <AppSideBar />
                <main className="w-full">
                    <SidebarTrigger />
                    <Outlet />
                </main>
            </SidebarProvider>
            <Footer />
        </>
    )
}

export default DashboardLayout;