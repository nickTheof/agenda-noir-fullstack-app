import {z} from "zod/v4";

export const passwordSchema = z.string()
    .min(8, 'Password must be at least 8 characters')
    .regex(/[A-Z]/, 'Must contain at least one uppercase letter')
    .regex(/[a-z]/, 'Must contain at least one lowercase letter')
    .regex(/[0-9]/, 'Must contain at least one number')
    .regex(/[@#$%!^&*]/, 'Must contain at least one special character')

export const passwordRecoveryRequestSchema = z.object({
    username: z.email().min(1, "Username has to be a valid email address"),
})

export const passwordResetSchema = z.object({
    newPassword: passwordSchema,
    confirmPassword: z.string(),
}).refine(data => data.newPassword === data.confirmPassword, {
    message: "Passwords don't match",
    path: ['confirmPassword'],
})

export const loginSchema = z.object({
    username: z.email().min(1, "Username is required"),
    password: z.string().min(1, "Password is required"),
})

export const registerSchema = z.object({
    username: z.email("Username must be a valid email address").min(1, "Username is required"),
    password: passwordSchema,
    confirmPassword: z.string(),
    firstname: z.string().min(1, "First name is required"),
    lastname: z.string().min(1, "First name is required")}
).refine((data) => data.password === data.confirmPassword, {
    message: "Passwords don't match",
    path: ['confirmPassword'],
});


export const changePasswordSchema = z.object({
    oldPassword: passwordSchema,
    newPassword: passwordSchema,
    confirmPassword: z.string().min(1, "Password is required"),
}).refine(
    data => data.newPassword === data.confirmPassword, {
        message: "Passwords don't match",
        path: ['confirmPassword'],
    }
)

export const projectCreateSchema = z.object({
    name: z.string().trim().min(1, "Name is required"),
    description: z.string().trim().min(1, "Description is required").trim(),
    status: z.string().trim().regex(/^(OPEN|ON_GOING|CLOSED)$/)
})


export const ticketSchema = z.object({
    title: z.string().min(1, "Title is required"),
    description: z.string().min(1, "Description is required"),
    priority: z.string().regex(/^(LOW|MEDIUM|HIGH|CRITICAL)$/),
    status: z.string().regex(/^(OPEN|ON_GOING|CLOSED)$/),
    expiryDate: z.string()
})


export const roleSchema = z.object({
    name: z.string().min(1, "Name is required"),
    permissions: z.array(z.string()).min(1, "At least one permission is required"),
});

export const roleUpdateSchema = roleSchema.omit({"name": true});

export const userRolesFormSchema = z.object({
    roleNames: z.array(z.string()),
})