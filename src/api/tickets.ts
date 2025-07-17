import type {PaginatedResponse, Ticket, TicketFormFields} from "@/core/types.ts";
const BASE_URL: string = import.meta.env.VITE_API_URL;

export async function getUserProjectTicketsPaginated(
    userUuid: string,
    projectUuid: string,
    accessToken: string,
    page: number,
    limit: number,
): Promise<PaginatedResponse<Ticket>> {
    const res = await fetch(`${BASE_URL}/users/${userUuid}/projects/${projectUuid}/tickets/filtered`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${accessToken}`,
        },
        body: JSON.stringify({
            page: page - 1,
            size: limit,
        })
    });
    if (!res.ok) {
        let detail = "Fetch user tickets paginated for specific project failed";
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

export async function createTicket(
    userUuid: string,
    projectUuid: string,
    ticket: TicketFormFields,
    accessToken: string,
): Promise<Ticket> {

    const res = await fetch(`${BASE_URL}/users/${userUuid}/projects/${projectUuid}/tickets`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${accessToken}`
        },
        body: JSON.stringify(ticket)
    });
    if (!res.ok) {
        let detail = "Inserting ticket for specific project failed";
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

export async function updateTicket(
    userUuid: string,
    projectUuid: string,
    ticketUuid: string,
    ticket: TicketFormFields,
    accessToken: string,
): Promise<Ticket> {

    const res = await fetch(`${BASE_URL}/users/${userUuid}/projects/${projectUuid}/tickets/${ticketUuid}`, {
        method: 'PATCH',
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${accessToken}`
        },
        body: JSON.stringify(ticket)
    });
    if (!res.ok) {
        let detail = "Updating ticket for specific project failed";
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


export async function getTicketByUuid(
    userUuid: string,
    projectUuid: string,
    ticketUuid: string,
    accessToken: string,
): Promise<Ticket> {
    const res = await fetch(`${BASE_URL}/users/${userUuid}/projects/${projectUuid}/tickets/${ticketUuid}`, {
        method: 'GET',
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${accessToken}`,
        }
    })
    if (!res.ok) {
        let detail = `Fetch ticket with uuid ${ticketUuid} failed`;
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


export async function deleteTicketByUuid(
    userUuid: string,
    projectUuid: string,
    ticketUuid: string,
    accessToken: string,
): Promise<void> {
    const res = await fetch(`${BASE_URL}/users/${userUuid}/projects/${projectUuid}/tickets/${ticketUuid}`, {
        method: 'DELETE',
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${accessToken}`,
        }
    })
    if (!res.ok) {
        let detail = `Delete ticket with uuid ${ticketUuid} failed`;
        try {
            const data = await res.json();
            if (typeof data?.message =="string") detail = data?.message;
        } catch (err) {
            console.error(err);
        }
        throw new Error(detail);
    }
}