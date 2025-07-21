import {Tabs, TabsList, TabsTrigger, TabsContent} from "@/components/ui/tabs.tsx";
import usePageTitle from "@/hooks/usePageTitle";
import {useAuth} from "@/hooks/useAuth.tsx";
import {useEffect, useState} from "react";
import type {RegisterUserResponse} from "@/core/types.ts";
import {getUserProfile} from "@/api/users.ts";
import LoaderComponent from "@/components/LoaderComponent.tsx";
import ProfileCardComponent from "@/components/ProfileCardComponent.tsx";
import ChangeUserPasswordComponent from "@/components/ChangeUserPasswordComponent.tsx";
import AccountActionsComponent from "@/components/AccountActionsComponent.tsx";

export default function SettingsPage() {
    usePageTitle("Settings");
    const {
        accessToken,
        userUuid,
        logout
    } = useAuth();
    const [profile, setProfile] = useState<RegisterUserResponse | null>(null)
    const [loading, setLoading] = useState<boolean>(true)

    useEffect(() => {
        if (!accessToken || !userUuid) return
        getUserProfile(accessToken, userUuid)
            .then((data) => setProfile(data))
            .catch((err) => {
                console.error("Error fetching user profile:", err)
            })
            .finally(() => setLoading(false))
    }, [accessToken, userUuid])

    if (loading) {
        return (
            <LoaderComponent title="Loading..." subtitle="Fetching user profile, stand by." />
        )
    }

    if (!profile) {
        return (
            <div className="p-8 text-center text-sm text-muted-foreground">
                Unable to load profile information.
            </div>
        )
    }

    return (
        <div className="flex w-full max-w-md flex-col gap-6 mx-auto">
            <h1 className="text-3xl font-bold">Account Settings</h1>

            <Tabs defaultValue="profile">
                <TabsList className="grid w-full grid-cols-3">
                    <TabsTrigger value="profile">Profile</TabsTrigger>
                    <TabsTrigger value="password">Password</TabsTrigger>
                    <TabsTrigger value="account">Account</TabsTrigger>
                </TabsList>

                <TabsContent value="profile">
                    <ProfileCardComponent
                        uuid={profile.uuid}
                        username={profile.username}
                        firstname={profile.firstname}
                        lastname={profile.lastname}
                        enabled={profile.enabled}
                        verified={profile.verified}
                    />
                </TabsContent>
                <TabsContent value="password">
                    <ChangeUserPasswordComponent accessToken={accessToken} logout={logout} />
                </TabsContent>

                <TabsContent value="account">
                    <AccountActionsComponent accessToken={accessToken} userUuid={userUuid} logout={logout}/>
                </TabsContent>
            </Tabs>
        </div>
    )
}