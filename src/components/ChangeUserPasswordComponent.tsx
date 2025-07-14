import {useForm} from "react-hook-form";
import type {ChangePasswordFields} from "@/core/types.ts";
import {zodResolver} from "@hookform/resolvers/zod";
import {changePasswordSchema} from "@/core/zod-schemas.ts";
import {changeUserPassword} from "@/api/users.ts";
import {toast} from "sonner";
import {Card, CardContent, CardDescription, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {Label} from "@radix-ui/react-label";
import {Button} from "@/components/ui/button.tsx";
import {useNavigate} from "react-router";
import PasswordInputComponent from "@/components/PasswordInputComponent.tsx";

type ChangeUserPasswordProps = {
    accessToken: string | null;
    logout: () => void;
}

const ChangeUserPasswordComponent = ({
    accessToken,
    logout
}: ChangeUserPasswordProps) => {

    const {
        register,
        handleSubmit,
        formState: { errors, isSubmitting },
        reset,
    } = useForm<ChangePasswordFields>(
        {
            resolver: zodResolver(changePasswordSchema)
        }
    )

    const navigate = useNavigate();

    const onSubmit = async (data: ChangePasswordFields) => {
        if (!accessToken) return;
        try {
            await changeUserPassword(data, accessToken);
            toast.success("User password changed successfully. You should login again with your new password!");
            logout();
            navigate("/auth/login", { replace: true });
        } catch (err) {
            toast.error(err instanceof Error ? err.message : "Change user password failed");
            reset();
        }
    }

    return (
        <>
            <Card className="w-full max-w-md mx-auto my-4">
                <CardHeader>
                    <CardTitle>Change your password</CardTitle>
                    <CardDescription>
                        Fill the below form to change your password
                    </CardDescription>
                </CardHeader>
                <CardContent>
                    <form onSubmit={handleSubmit(onSubmit)}>
                        <div className="flex flex-col gap-6">
                            <div className="grid gap-2">
                                <Label htmlFor="oldPassword">Current Password</Label>
                                <PasswordInputComponent<ChangePasswordFields>
                                    id="oldPassword"
                                    name="oldPassword"
                                    register={register}
                                    disabled={isSubmitting}
                                    placeholder="Enter your old password"
                                />
                                {errors.oldPassword && (
                                    <div className="text-red-500 dark:text-red-400">{errors.oldPassword.message}</div>
                                )}
                            </div>
                            <div className="grid gap-2">
                                <Label htmlFor="newPassword">New Password</Label>
                                <PasswordInputComponent<ChangePasswordFields>
                                    id="newPassword"
                                    name="newPassword"
                                    register={register}
                                    disabled={isSubmitting}
                                    placeholder="Enter your new password"
                                />
                                {errors.newPassword && (
                                    <div className="text-red-500 dark:text-red-400">{errors.newPassword.message}</div>
                                )}
                            </div>
                            <div className="grid gap-2">
                                <Label htmlFor="confirmPassword">New Password Confirmation</Label>
                                <PasswordInputComponent<ChangePasswordFields>
                                    id="confirmPassword"
                                    name="confirmPassword"
                                    register={register}
                                    disabled={isSubmitting}
                                    placeholder="Confirm your new password"
                                />
                                {errors.confirmPassword && (
                                    <div className="text-red-500 dark:text-red-400">{errors.confirmPassword.message}</div>
                                )}
                            </div>
                            <Button type="submit" disabled={isSubmitting} className="w-full">
                                {isSubmitting ? 'Submitting...' : 'Submit'}
                            </Button>
                        </div>
                    </form>
                </CardContent>
            </Card>
        </>
    )
}

export default ChangeUserPasswordComponent;