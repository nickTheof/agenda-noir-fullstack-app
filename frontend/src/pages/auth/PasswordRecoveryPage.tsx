import {Link, useNavigate} from "react-router";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import usePageTitle from "@/hooks/usePageTitle.tsx";
import {Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {Label} from "@radix-ui/react-label";
import {Input} from "@/components/ui/input.tsx";
import {Button} from "@/components/ui/button.tsx";
import {toast} from "sonner";
import {sendPasswordRecoveryRequest} from "@/api/auth.ts";
import type {PasswordRecoveryRequestFields} from "@/core/types.ts";
import {passwordRecoveryRequestSchema} from "@/core/zod-schemas.ts";


const PasswordRecoveryPage = () => {
    usePageTitle("Password Recovery")
    const navigate = useNavigate();
    const {
        register,
        handleSubmit,
        formState: { errors, isSubmitting },
        reset
    } = useForm<PasswordRecoveryRequestFields>({
        resolver: zodResolver(passwordRecoveryRequestSchema)
    });

    const onSubmit = async (data: PasswordRecoveryRequestFields) => {
        try {
            const res = await sendPasswordRecoveryRequest(data.username);
            toast.success(res.message);
            navigate("/");
        } catch (err) {
            toast.error(err instanceof Error ? err.message : "Unable to send password recovery request.");
            reset();
        }
    }

    return (
        <>
            <Card className="w-full max-w-sm mx-auto my-4">
                <CardHeader>
                    <CardTitle>Password Recovery Request</CardTitle>
                    <CardDescription>
                        Do you forget your password? Enter your username below to send you a link to your email for resetting your password
                    </CardDescription>
                </CardHeader>
                <CardContent>
                    <form onSubmit={handleSubmit(onSubmit)}>
                        <div className="flex flex-col gap-6">
                            <div className="grid gap-2">
                                <Label htmlFor="username">Email</Label>
                                <Input id="username"
                                       placeholder="Enter your email..."
                                       autoFocus
                                       {...register("username")}
                                       disabled={isSubmitting}
                                       autoComplete="off"
                                />
                                {errors.username && (
                                    <div className="text-red-500 dark:text-red-400">{errors.username.message}</div>
                                )}
                            </div>
                            <Button type="submit" disabled={isSubmitting} className="w-full">
                                {isSubmitting ? 'Submitting...' : 'Submit'}
                            </Button>
                        </div>
                    </form>
                </CardContent>
                <CardFooter className="flex justify-center w-full mt-4">
                    <p className="text-sm text-muted-foreground">
                        Did you remember your password?{' '}
                        <Link to="/auth/login" className="text-primary hover:underline underline-offset-4">
                            Login
                        </Link>
                    </p>
                </CardFooter>
            </Card>
        </>
    )
}

export default PasswordRecoveryPage;