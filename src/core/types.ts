import {z} from "zod/v4";
import {
    loginSchema,
    passwordRecoveryRequestSchema,
    passwordResetSchema,
    registerSchema,
    changePasswordSchema,
    projectCreateSchema,
    ticketSchema, roleSchema, roleUpdateSchema
} from "@/core/zod-schemas.ts";

export type PasswordRecoveryRequestFields = z.infer<typeof passwordRecoveryRequestSchema>;
export type PasswordResetFields = z.infer<typeof passwordResetSchema>
export type LoginFields = z.infer<typeof loginSchema>;
export type RegisterFields = z.infer<typeof registerSchema>
export type ChangePasswordFields = z.infer<typeof changePasswordSchema>
export type ProjectFormField = z.infer<typeof projectCreateSchema>
export type TicketFormFields = z.infer<typeof ticketSchema>;
export type RoleFormFields = z.infer<typeof roleSchema>;
export type RoleUpdateFields = z.infer<typeof roleUpdateSchema>;

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

export type Resource = "ROLE" | "USER" | "PROJECT" | "TICKET";
export type Action = "READ" | "CREATE" | "UPDATE" | "DELETE";
export type PermissionName = `${Action}_${Resource}`

export type PermissionResponse = {
    id: number;
    name: PermissionName;
    resource: Resource;
    action: Action;
};

export type RoleResponse = {
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
