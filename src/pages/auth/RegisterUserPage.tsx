import {Link, useNavigate} from "react-router";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import usePageTitle from "@/hooks/usePageTitle.tsx";
import {Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {Label} from "@radix-ui/react-label";
import {Input} from "@/components/ui/input.tsx";
import {Button} from "@/components/ui/button.tsx";
import {toast} from "sonner";
import {registerUser} from "@/api/auth.ts";
import type {RegisterFields} from "@/core/types.ts";
import {registerSchema} from "@/core/zod-schemas.ts";
import PasswordInputComponent from "@/components/PasswordInputComponent.tsx";


const RegisterUserPage = () => {
    usePageTitle("Register User");
    const navigate = useNavigate();
    const {
        register,
        handleSubmit,
        reset,
        formState: { errors, isSubmitting },
    } = useForm<RegisterFields>({
        resolver: zodResolver(registerSchema)
    });

    const onSubmit = async(data: RegisterFields) => {
        try {
            await registerUser(data);
            toast.success("User registered successfully");
            navigate("/auth/login", {
                replace: true,
            });
        } catch (err) {
            toast.error(err instanceof Error ? err.message : "User registration failed");
            reset();
        }
    }

    return (
        <>
            <Card className="w-full max-w-sm mx-auto my-4">
                <CardHeader>
                    <CardTitle>Create your account</CardTitle>
                    <CardDescription>
                        Fill the below form to register your account
                    </CardDescription>
                </CardHeader>
                <CardContent>
                    <form onSubmit={handleSubmit(onSubmit)}>
                        <div className="flex flex-col gap-6">
                            <div className="grid gap-2">
                                <Label htmlFor="username">Email</Label>
                                <Input id="username"
                                       placeholder="Enter your email"
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
                                <Label htmlFor="firstname">Firstname</Label>
                                <Input id="firstname"
                                       placeholder="Enter your firstname"
                                       {...register("firstname")}
                                       disabled={isSubmitting}
                                />
                                {errors.firstname && (
                                    <div className="text-red-500 dark:text-red-400">{errors.firstname.message}</div>
                                )}
                            </div>
                            <div className="grid gap-2">
                                <Label htmlFor="lastname">Lastname</Label>
                                <Input id="lastname"
                                       placeholder="Enter your lastname"
                                       {...register("lastname")}
                                       disabled={isSubmitting}
                                />
                                {errors.lastname && (
                                    <div className="text-red-500 dark:text-red-400">{errors.lastname.message}</div>
                                )}
                            </div>
                            <div className="grid gap-2">
                                <Label htmlFor="password">Password</Label>
                                <PasswordInputComponent
                                    id="password"
                                    name="password"
                                    register={register}
                                    isSubmitting={isSubmitting}
                                    placeholder="Enter your password"
                                    autoComplete="new-password"
                                />
                                {errors.password && (
                                    <div className="text-red-500 dark:text-red-400">{errors.password.message}</div>
                                )}
                            </div>
                            <div className="grid gap-2">
                                <Label htmlFor="confirmPassword">Password Confirmation</Label>
                                <PasswordInputComponent<RegisterFields>
                                    id="confirmPassword"
                                    name="confirmPassword"
                                    register={register}
                                    isSubmitting={isSubmitting}
                                    placeholder="Enter your password again"
                                    autoComplete="off"
                                />
                                {errors.confirmPassword && (
                                    <div className="text-red-500 dark:text-red-400">{errors.confirmPassword.message}</div>
                                )}
                            </div>
                            <Button type="submit" disabled={isSubmitting} className="w-full">
                                {isSubmitting ? 'Registering...' : 'Register'}
                            </Button>
                        </div>
                    </form>
                </CardContent>
                <CardFooter className="flex justify-center w-full mt-4">
                    <p className="text-sm text-muted-foreground">
                        Already have an account?{' '}
                        <Link to="/auth/login" className="text-primary hover:underline underline-offset-4">
                            Login
                        </Link>
                    </p>
                </CardFooter>
            </Card>
        </>
    )
}

export default RegisterUserPage;