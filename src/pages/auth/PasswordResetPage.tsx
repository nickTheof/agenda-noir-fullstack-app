import {useSearchParams, Navigate, Link, useNavigate} from "react-router";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import usePageTitle from "@/hooks/usePageTitle.tsx";
import {Card, CardContent, CardFooter, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {Label} from "@radix-ui/react-label";
import {Input} from "@/components/ui/input.tsx";
import {Button} from "@/components/ui/button.tsx";
import {toast} from "sonner";
import {sendPasswordReset} from "@/api/auth.ts";
import type {PasswordResetFields} from "@/core/types.ts";
import {passwordResetSchema} from "@/core/zod-schemas.ts";


const PasswordResetPage = () => {
    usePageTitle("Reset Password");
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const {
        register,
        handleSubmit,
        formState: { errors, isSubmitting },
    } = useForm<PasswordResetFields>({
        resolver: zodResolver(passwordResetSchema)
    })

    const token = searchParams.get("token");

    const onSubmit = async (data: PasswordResetFields) => {
        try {
            const res = await sendPasswordReset(token ?? "", data);
            toast.success(res.message);
        } catch (err) {
            toast.error(err instanceof Error ? err.message : "Password reset failed");
        }
        navigate("/auth/login", {
            replace: true,
        })
    }

    if (!token) {
        return <Navigate to="/" replace />
    }

    return (
        <>
            <Card className="w-full max-w-sm mx-auto my-4">
                <CardHeader>
                    <CardTitle>Reset your password</CardTitle>
                </CardHeader>
                <CardContent>
                    <form onSubmit={handleSubmit(onSubmit)}>
                        <div className="flex flex-col gap-6">
                            <div className="grid gap-2">
                                <Label htmlFor="password">Password</Label>
                                <Input id="password"
                                       type="password"
                                       placeholder="Enter your new password"
                                       {...register("newPassword")}
                                       disabled={isSubmitting}
                                />
                                {errors.newPassword && (
                                    <div className="text-red-500 dark:text-red-400">{errors.newPassword.message}</div>
                                )}
                            </div>
                            <div className="grid gap-2">
                                <Label htmlFor="confirmPassword">Password Confirmation</Label>
                                <Input id="confirmPassword"
                                       type="password"
                                       placeholder="Enter your new password again"
                                       {...register("confirmPassword")}
                                       disabled={isSubmitting}
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
                <CardFooter className="flex-col">
                   <Button variant="outline" className="w-full cursor-default" asChild>
                       <Link to="/">Cancel</Link>
                   </Button>
                </CardFooter>
            </Card>
        </>
    )
}

export default PasswordResetPage;