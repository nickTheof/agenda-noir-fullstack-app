import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card.tsx";
import { cn } from "@/lib/utils.ts";
import { Badge } from "@/components/ui/badge.tsx";

export type ProfileCardProps = {
    uuid: string;
    username: string;
    firstname: string;
    lastname: string;
    enabled: boolean;
    verified: boolean;
}

const ProfileCardComponent = (
    {
        uuid,
        username,
        firstname,
        lastname,
        enabled,
        verified,
    }: ProfileCardProps
) => {
    return (
        <>
            <Card
                className="w-full max-w-md mx-auto"
            >
                <CardHeader
                    className="flex items-center justify-between"
                >
                    <CardTitle
                        className="text-xl font-semibold text-foreground"
                    >
                        {firstname} {lastname}
                    </CardTitle>
                    <div
                        className="flex gap-2 pt-2"
                    >
                        <Badge
                            className={cn(
                                "text-xs",
                                enabled
                                    ? "bg-green-600 text-white hover:bg-green-700"
                                    : "bg-gray-600 text-white hover:bg-gray-700"
                            )}
                        >
                            {enabled ? "Enabled" : "Disabled"}
                        </Badge>
                        <Badge
                            className={cn(
                                "text-xs",
                                verified
                                    ? "bg-blue-600 text-white hover:bg-blue-700"
                                    : "bg-gray-600 text-white hover:bg-gray-700"
                            )}
                        >
                            {verified ? "Verified" : "Unverified"}
                        </Badge>
                    </div>
                </CardHeader>
                <CardContent
                    className="space-y-3 text-sm text-muted-foreground"
                >
                    <div>
                        <p className="text-xs uppercase tracking-wide text-muted-foreground">
                            Email
                        </p>
                        <p className="text-foreground">{username}</p>
                    </div>
                    <div>
                        <p className="text-xs uppercase tracking-wide text-muted-foreground">
                            UUID
                        </p>
                        <p className="break-all text-foreground">{uuid}</p>
                    </div>
                </CardContent>
            </Card>
        </>
    )
}

export default ProfileCardComponent;