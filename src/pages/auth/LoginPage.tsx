import {Link, useNavigate} from "react-router";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import usePageTitle from "@/hooks/usePageTitle.tsx";
import {useAuth} from "@/hooks/useAuth.tsx";
import {
    Card,
    CardAction,
    CardContent,
    CardDescription,
    CardHeader,
    CardTitle
} from "@/components/ui/card.tsx";
import {Button} from "@/components/ui/button.tsx";
import {Label} from "@radix-ui/react-label";
import {Input} from "@/components/ui/input.tsx";
import {toast} from "sonner";
import type {LoginFields} from "@/core/types.ts";
import {loginSchema} from "@/core/zod-schemas.ts";
import PasswordInputComponent from "@/components/PasswordInputComponent.tsx";


const LoginPage = () => {
    usePageTitle("Login Page");
    const { loginUser } = useAuth();
    const navigate = useNavigate();

    const {
        register,
        handleSubmit,
        resetField,
        formState: {errors, isSubmitting}
    } = useForm<LoginFields>(
        {
            resolver: zodResolver(loginSchema)
        }
    );

    const onSubmit = async (data: LoginFields) => {
        try {
            await loginUser(data);
            toast.success("Login successfully");
            navigate("/dashboard/my-profile"); //TODO: Fix the navigation link when the protected dashboard has been finalized
        } catch (err) {
            toast.error(err instanceof Error ? err.message : "Login failed");
            resetField("password");
        }

    }
    return (
        <>
            <Card className="w-full max-w-sm mx-auto my-4">
                <CardHeader>
                    <CardTitle>Login to your account</CardTitle>
                    <CardDescription>
                        Enter your email below to login to your account
                    </CardDescription>
                    <CardAction>
                        <Button variant="link" asChild>
                            <Link to="/auth/register">Sign Up</Link>
                        </Button>
                    </CardAction>
                </CardHeader>
                <CardContent>
                    <form onSubmit={handleSubmit(onSubmit)}>
                        <div className="flex flex-col gap-6">
                            <div className="grid gap-2">
                                <Label htmlFor="username">Email</Label>
                                <Input
                                    id="username"
                                    type="text"
                                    placeholder="m@example.com"
                                    autoFocus
                                    {...register("username")}
                                    disabled={isSubmitting}
                                    autoComplete="off"
                                />
                                {errors.username && (
                                    <div className="text-red-500 dark:text-red-400">{errors.username.message}</div>
                                )}
                            </div>
                            <div className="grid gap-2">
                                <div className="flex items-center">
                                    <Label htmlFor="password">Password</Label>
                                    <Link
                                        to="/auth/password-recovery"
                                        className="ml-auto inline-block text-sm underline-offset-4 hover:underline"
                                    >
                                        Forgot your password?
                                    </Link>
                                </div>
                                <PasswordInputComponent<LoginFields>
                                    id="password"
                                    name="password"
                                    register={register}
                                    disabled={isSubmitting}
                                    placeholder="Enter your password"
                                />
                                {errors.password && (
                                    <div className="text-red-500 dark:text-red-400">{errors.password.message}</div>
                                )}
                            </div>
                            <Button type="submit" disabled={isSubmitting} className="w-full">
                                {isSubmitting ? 'Logging...' : 'Login'}
                            </Button>
                        </div>
                    </form>
                </CardContent>
            </Card>
        </>
    )
}

export default LoginPage;