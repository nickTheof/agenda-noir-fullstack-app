import {Label} from "@radix-ui/react-label";
import {Input} from "@/components/ui/input.tsx";
import PasswordInputComponent from "@/components/PasswordInputComponent.tsx";
import type {RegisterFields} from "@/core/types.ts";
import {Button} from "@/components/ui/button.tsx";
import { useForm } from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {registerSchema} from "@/core/zod-schemas.ts";
import {registerUser} from "@/api/auth.ts";
import {toast} from "sonner";
import {useNavigate} from "react-router";
import {useAuth} from "@/hooks/useAuth.tsx";
import {insertUser} from "@/api/users.ts";


type UserCreateFormComponentProps = {
    mode: "protected" | "open"
}

const UserCreateFormComponent = (
    {mode = "open"}: UserCreateFormComponentProps
) => {

    const {
        register,
        handleSubmit,
        formState: { errors, isSubmitting },
        reset
    } = useForm<RegisterFields>({
        resolver: zodResolver(registerSchema)
    });

    const navigate = useNavigate();
    const {accessToken} = useAuth();

    const onSubmit = async(data: RegisterFields) => {
        if (mode === "open") {
            try {
                await registerUser(data);
                toast.success("User registered successfully. An email has been sent to your inbox for verifying your registration.");
                navigate("/auth/login", {
                    replace: true,
                });
            } catch (err) {
                toast.error(err instanceof Error ? err.message : "User registration failed");
                reset();
            }
        } else {
            try {
                await insertUser(accessToken ?? "", data);
                toast.success("User inserted successfully");
                navigate("../", {
                    replace: true,
                });
            } catch (err) {
                toast.error(err instanceof Error ? err.message : "User insertion failed");
                reset();
            }
        }
    }


    return (
        <form onSubmit={handleSubmit(onSubmit)}>
            <div className="flex flex-col gap-6">
                <div className="grid gap-2">
                    <Label htmlFor="username">Email</Label>
                    <Input id="username"
                           placeholder={`Enter ${mode === "open" ? "your" : "user"} email`}
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
                           placeholder={`Enter ${mode === "open" ? "your" : "user"} firstname`}
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
                           placeholder={`Enter ${mode === "open" ? "your" : "user"} lastname`}
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
                        placeholder={`Enter ${mode === "open" ? "your" : "user"} password`}
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
                        placeholder={`Enter ${mode === "open" ? "your" : "user"} password again`}
                        autoComplete="off"
                    />
                    {errors.confirmPassword && (
                        <div className="text-red-500 dark:text-red-400">{errors.confirmPassword.message}</div>
                    )}
                </div>
                {mode === "open" ? (
                    <Button type="submit" disabled={isSubmitting} className="w-full">
                        {isSubmitting ? 'Registering...' : 'Register'}
                    </Button>
                ) : (
                    <Button type="submit" disabled={isSubmitting} className="w-full">
                        {isSubmitting ? 'Submitting...' : 'Submit'}
                    </Button>
                )}
            </div>
        </form>
    )
}

export default UserCreateFormComponent;