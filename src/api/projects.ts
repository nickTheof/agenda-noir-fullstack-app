import type {PaginatedResponse, Project, ProjectFormField} from "@/core/types.ts";
const BASE_URL: string = import.meta.env.VITE_API_URL;

export async function getUserProjects(
    userUuid: string,
    accessToken: string,
): Promise<Project[]> {
    const res = await fetch(`${BASE_URL}/users/${userUuid}/projects`, {
        method: 'GET',
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${accessToken}`,
        }
    })
    if (!res.ok) {
        let detail = "Fetch user projects failed";
        try {
            const data = await res.json();
            if (typeof data?.message =="string") detail = data?.message;
        } catch (err) {
            console.error(err);
        }
        throw new Error(detail);
    }
    return await res.json();
}

export async function getUserProjectsPaginated(
    userUuid: string,
    accessToken: string,
    page: number,
    limit: number,
): Promise<PaginatedResponse<Project>> {
    const res = await fetch(`${BASE_URL}/users/${userUuid}/projects/filtered`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${accessToken}`,
        },
        body: JSON.stringify({
            page: page-1,
            size: limit,
        })
    })
    if (!res.ok) {
        let detail = "Fetch user projects paginated failed";
        try {
            const data = await res.json();
            if (typeof data?.message =="string") detail = data?.message;
        } catch (err) {
            console.error(err);
        }
        throw new Error(detail);
    }
    return await res.json();
}

export async function getUserProjectByUuid(
    userUuid: string,
    projectUuid: string,
    accessToken: string,
): Promise<Project> {
    const res = await fetch(`${BASE_URL}/users/${userUuid}/projects/${projectUuid}`, {
        method: 'GET',
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${accessToken}`,
        }
    })
    if (!res.ok) {
        let detail = `Fetch project with uuid ${projectUuid} failed`;
        try {
            const data = await res.json();
            if (typeof data?.message =="string") detail = data?.message;
        } catch (err) {
            console.error(err);
        }
        throw new Error(detail);
    }
    return await res.json();
}

export async function createUserProject(
    userUuid: string,
    project: ProjectFormField,
    accessToken: string,
): Promise<Project> {
    const res = await fetch(`${BASE_URL}/users/${userUuid}/projects`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${accessToken}`,
        },
        body: JSON.stringify(project),
    })
    if (!res.ok) {
        let detail = `Create a new project for user with uuid ${userUuid} failed`;
        try {
            const data = await res.json();
            if (typeof data?.message =="string") detail = data?.message;
        } catch (err) {
            console.error(err);
        }
        throw new Error(detail);
    }
    return await res.json();
}

export async function updateUserProject(
    userUuid: string,
    projectUuid: string,
    project: ProjectFormField,
    accessToken: string,
): Promise<Project> {
    const res = await fetch(`${BASE_URL}/users/${userUuid}/projects/${projectUuid}`, {
        method: 'PATCH',
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${accessToken}`,
        },
        body: JSON.stringify(project),
    })
    if (!res.ok) {
        let detail = `Update project with uuid ${projectUuid} failed`;
        try {
            const data = await res.json();
            if (typeof data?.message =="string") detail = data?.message;
        } catch (err) {
            console.error(err);
        }
        throw new Error(detail);
    }
    return await res.json();
}

export async function deleteUserProject(
    userUuid: string,
    projectUuid: string,
    accessToken: string,
): Promise<void> {
    const res = await fetch(`${BASE_URL}/users/${userUuid}/projects/${projectUuid}`, {
        method: 'DELETE',
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${accessToken}`,
        }
    })
    if (!res.ok) {
        let detail = `Delete project with uuid ${projectUuid} failed`;
        try {
            const data = await res.json();
            if (typeof data?.message =="string") detail = data?.message;
        } catch (err) {
            console.error(err);
        }
        throw new Error(detail);
    }
}