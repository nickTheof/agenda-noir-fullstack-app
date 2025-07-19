import {Home, Lock, Settings, Users} from "lucide-react";
import { Sidebar, SidebarContent, SidebarGroup, SidebarGroupLabel, SidebarGroupContent, SidebarMenu, SidebarMenuItem, SidebarMenuButton } from "@/components/ui/sidebar.tsx";
import { Link } from "react-router";
import {useAuth} from "@/hooks/useAuth.tsx";

// TODO: Add remaining items of Side Navbar
const items = [
    {
        title: "Projects",
        url: "/dashboard/projects/view",
        icon: Home,
        protection: ""
    },
    {
        title: "Roles",
        url: "/dashboard/roles",
        icon: Lock,
        protection: "READ_ROLE"
    },
    {
        title: "Users",
        url: "/dashboard/users",
        icon: Users,
        protection: "READ_USER"
    },
    {
        title: "Settings",
        url: "/dashboard/profile/settings",
        icon: Settings,
        protection: ""
    },
]

const AppSideBar = () => {
    const {userHasAuthority} = useAuth();
    return (
        <Sidebar>
            <SidebarContent>
                <SidebarGroup>
                    <SidebarGroupLabel>Menu</SidebarGroupLabel>
                    <SidebarGroupContent>
                        <SidebarMenu>
                            {items
                                .filter(item => !item.protection || userHasAuthority(item.protection))
                                .map(item => (
                                    <SidebarMenuItem key={item.title}>
                                        <SidebarMenuButton asChild>
                                            <Link to={item.url}>
                                                <item.icon />
                                                <span>{item.title}</span>
                                            </Link>
                                        </SidebarMenuButton>
                                    </SidebarMenuItem>
                                ))}
                        </SidebarMenu>
                    </SidebarGroupContent>
                </SidebarGroup>
            </SidebarContent>
        </Sidebar>
    )
}

export default AppSideBar;