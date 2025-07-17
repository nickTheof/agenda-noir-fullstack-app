import {z} from "zod/v4";
import {
    loginSchema,
    passwordRecoveryRequestSchema,
    passwordResetSchema,
    registerSchema,
    changePasswordSchema,
    projectCreateSchema,
    ticketSchema
} from "@/core/zod-schemas.ts";

export type PasswordRecoveryRequestFields = z.infer<typeof passwordRecoveryRequestSchema>;
export type PasswordResetFields = z.infer<typeof passwordResetSchema>
export type LoginFields = z.infer<typeof loginSchema>;
export type RegisterFields = z.infer<typeof registerSchema>
export type ChangePasswordFields = z.infer<typeof changePasswordSchema>
export type ProjectFormField = z.infer<typeof projectCreateSchema>
export type TicketFormFields = z.infer<typeof ticketSchema>;

export type LoginResponse = {
    token: string
}

export type ApiMessageResponse = {
    status: number,
    message: string
}

export type RegisterUserResponse = {
    id: number;
    uuid: string;
    username: string;
    firstname: string;
    lastname: string;
    enabled: boolean;
    verified: boolean;
    isDeleted: boolean;
    loginConsecutiveFailAttempts: number;
}

export type JwtPayload = {
    sub?: string;
    userUuid?: string;
    exp?: number;
}

type PermissionResponse = {
    id: number;
    name: string;
    resource: string;
    action: string;
}

type RoleResponse = {
    id: number;
    name: string;
    permissions: PermissionResponse[];
}

export type UserRoleResponse = RoleResponse[];

export type Project = {
    id: number;
    uuid: string;
    name: string;
    description: string;
    ownerUuid: string;
    status: ProjectStatus;
    deleted: boolean;
}

export type ProjectStatus = 'OPEN' | 'ON_GOING' | 'CLOSED';

export interface PaginatedResponse<T> {
    data: T[];
    totalItems: number;
    totalPages: number;
    numberOfElements: number;
    currentPage: number;
    pageSize: number;
}

export type Ticket = {
    id: string;
    uuid: string;
    title: string;
    description: string;
    priority: string;
    status: string;
    expiryDate: string;
}
