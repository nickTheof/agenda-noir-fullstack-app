import {type ComponentProps, useState} from "react";
import {type UseFormRegister, type FieldValues, type Path} from "react-hook-form";
import {Input} from "@/components/ui/input.tsx";
import {Button} from "@/components/ui/button.tsx";
import {EyeOff, Eye} from "lucide-react";


type PasswordInputProps<T extends FieldValues> = ComponentProps<"input"> & {
    register: UseFormRegister<T>
    name: Path<T>
    isSubmitting?: boolean,
    autoComplete?: "current-password" | "new-password" | "off"
}

const PasswordInputComponent = <T extends FieldValues>({
                                                           register,
                                                           name,
                                                           isSubmitting,
                                                           ...props
                                                       }: PasswordInputProps<T>) => {
    const [showPassword, setShowPassword] = useState(false)

    return (
        <div className="relative">
            <Input
                id={name}
                type={showPassword ? "text" : "password"}
                disabled={isSubmitting}
                className="pr-10"
                autoComplete="current-password"
                {...register(name)}
                {...props}
            />
            <Button
                type="button"
                variant="ghost"
                size="sm"
                className="absolute right-0 top-0 h-full px-3 py-2 hover:bg-transparent"
                onClick={() => setShowPassword(!showPassword)}
                disabled={isSubmitting}
            >
                {showPassword ? (
                    <EyeOff className="h-4 w-4" aria-hidden="true" />
                ) : (
                    <Eye className="h-4 w-4" aria-hidden="true" />
                )}
                <span className="sr-only">
          {showPassword ? "Hide password" : "Show password"}
        </span>
            </Button>
        </div>
    )
}

export default PasswordInputComponent;