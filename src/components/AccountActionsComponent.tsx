import {useState} from "react";
import {Card, CardContent, CardHeader} from "@/components/ui/card.tsx";
import {Button} from "@/components/ui/button.tsx";
import DeleteDialogComponent from "@/components/DeleteDialogComponent.tsx";
import {deactivateUserAccount, deleteUserAccount} from "@/api/users.ts";
import {toast} from "sonner";
import {useNavigate} from "react-router";

type AccountActionsComponentProps = {
    userUuid: string | null;
    accessToken: string | null;
    logout: () => void;
}

const AccountActionsComponent = (
    { userUuid, accessToken, logout }: AccountActionsComponentProps
) => {
    const [open, setOpen] = useState(false);
    const navigate = useNavigate();

    const handleDeactivate = async() => {
        if (!userUuid || !accessToken) return;
       try {
           await deactivateUserAccount(userUuid, accessToken);
           toast.success("Account Deactivated Successfully. If you want to activate again your account, feel free to contact with the administrator via email.");
           logout();
           navigate("/", {
               replace: true,
           });
       } catch (error) {
           toast.error(error instanceof Error ? error.message : "Unable to deactivate your account");
       }
    };

    const handleDelete = async() => {
        if (!userUuid || !accessToken) return;
        try {
            await deleteUserAccount(userUuid, accessToken);
            toast.success("Account Deleted Successfully.");
            logout();
            navigate("/", {
                replace: true,
            });
        } catch (error) {
            toast.error(error instanceof Error ? error.message : "Unable to delete your account");
        }
    }



    return (
        <>
        <Card className="border-destructive w-full max-w-md mx-auto my-4">
            <CardHeader>
                <h2 className="text-xl font-semibold">Account Actions</h2>
            </CardHeader>
            <CardContent className="space-y-6">
                <div className="border-t pt-4 space-y-4">
                    <div>
                        <h3 className="font-medium">Deactivate Account</h3>
                        <p className="text-sm text-muted-foreground">
                            Temporarily disable your account
                        </p>
                        <Button
                            variant="outline"
                            className="mt-2 w-40"
                            onClick={handleDeactivate}
                        >
                            Deactivate Account
                        </Button>
                    </div>

                    <div className="border-t pt-4">
                        <h3 className="font-medium text-destructive">Delete Account</h3>
                        <p className="text-sm text-muted-foreground">
                            Permanently remove your account and all data
                        </p>
                        <Button variant="destructive" className="mt-2 w-40" onClick={() => setOpen(true)}>
                            Delete Account
                        </Button>
                    </div>
                </div>
            </CardContent>
        </Card>
            <DeleteDialogComponent
                open={open}
                onOpenChange={(open) => setOpen(open)}
                onDelete={handleDelete}
                entity="account"
            />
        </>
    );
}

export default AccountActionsComponent;