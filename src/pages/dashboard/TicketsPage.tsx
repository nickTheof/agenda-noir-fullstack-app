import {useState, useEffect} from "react";
import {useLocation, useNavigate, useParams} from "react-router";
import usePageTitle from "@/hooks/usePageTitle.tsx";
import {useAuth} from "@/hooks/useAuth.tsx";
import LoaderComponent from "@/components/LoaderComponent.tsx";
import {toast} from "sonner";
import DeleteDialogComponent from "@/components/DeleteDialogComponent.tsx";
import {Button} from "@/components/ui/button.tsx";
import {Plus} from "lucide-react";
import {deleteTicketByUuid, getUserProjectTicketsPaginated} from "@/api/tickets.ts";
import TicketCardComponent from "@/components/TicketCardComponent";
import {getUserProjectByUuid} from "@/api/projects.ts";
import type {Project, Ticket} from "@/core/types.ts";
import CustomPaginationComponent, {type PaginationControlProps} from "@/components/CustomPaginationComponent.tsx";


const TicketsPage = () => {
    usePageTitle("Project's Tickets")
    const {accessToken, userUuid} = useAuth();
    const [tickets, setTickets] = useState<Ticket[]>([]);
    const [loading, setLoading] = useState<boolean>(true)
    const [projectDetails, setProjectDetails] = useState<Project | null>(null);
    const [pagination, setPagination] = useState<PaginationControlProps>({
        page: 1,
        limit: 6,
        totalItems: 0,
        totalPages: 1,
    })

    const [deleteState, setDeleteState] = useState<{
        isOpen: boolean;
        ticketUuid: string | null;
    }>({
        isOpen: false,
        ticketUuid: null,
    });

    const navigate = useNavigate();
    const { projectUuid } = useParams<{ projectUuid: string }>();

    const handleDelete = async () => {
        if (!deleteState.ticketUuid|| !userUuid || !accessToken || !projectUuid) return;
        try {
            await deleteTicketByUuid(userUuid, projectUuid, deleteState.ticketUuid, accessToken);
            toast.success("Ticket deleted successfully.");
            setTickets(prevState =>
                prevState.filter(ticket => ticket.uuid != deleteState.ticketUuid)
            )
        }
        catch (err) {
            toast.error(err instanceof Error ? err.message : "Ticket deleted failed");
        } finally {
            setDeleteState({ isOpen: false, ticketUuid: null });
        }
    };

    const handleAddTicket = () => {
        navigate("../tickets/new");
    }
    const location = useLocation()

    useEffect(() => {
        if (!userUuid || !accessToken || !projectUuid) return;
        getUserProjectByUuid(userUuid, projectUuid, accessToken).then((
            data => setProjectDetails(data)
        ));
        getUserProjectTicketsPaginated(userUuid, projectUuid, accessToken, pagination.page, pagination.limit)
            .then((data) => {
            setTickets(data.data);
            setPagination(prev => ({
                    ...prev,
                    total: data.totalItems,
                    totalPages: data.totalPages,
                }))
        }).catch(err => {
            toast.error(err instanceof Error ? err.message : "Fetching data failed");
            navigate("/dashboard/projects/view")
        }).finally(() => setLoading(false));
    }, [userUuid, projectUuid, accessToken, location, navigate, pagination.page, pagination.limit]);

    if (loading) {
        return <LoaderComponent
            title="Loading..."
            subtitle="Fetching user tickets of the specific project, stand by." />
    }
    return (
        <>
            <div className="w-full p-12 space-y-6">
                <div className="flex justify-center items-center space-x-32">
                    <h1 className="text-2xl font-bold">{projectDetails?.name || "Your Project"}</h1>
                    <Button onClick={handleAddTicket}>
                        <Plus className="mr-2 h-4 w-4" />
                        New Ticket
                    </Button>
                </div>
                {tickets.length > 0 ? (
                    <>
                        <div className="flex justify-center items-center px-4">
                            <ul className="flex flex-wrap justify-center gap-4">
                                {tickets.map((ticket) => (
                                    <li key={ticket.uuid}>
                                        <TicketCardComponent
                                            uuid={ticket.uuid}
                                            title={ticket.title}
                                            description={ticket.description}
                                            status={ticket.status}
                                            priority={ticket.priority}
                                            expiryDate={ticket.expiryDate}
                                            onDeleteClick={() => setDeleteState({isOpen: true, ticketUuid: ticket.uuid})}
                                        />
                                    </li>
                                ))}
                            </ul>
                        </div>
                        {tickets.length > 0 && pagination.totalPages > 1 && (
                            <CustomPaginationComponent pagination={pagination} setPagination={setPagination} />
                        )}
                        <DeleteDialogComponent
                            open={deleteState.isOpen}
                            onOpenChange={(open) => setDeleteState(prev => ({
                                ...prev,
                                isOpen: open,
                            }))}
                            onDelete={handleDelete}
                            entity="ticket"
                        />
                    </>
                ) : (
                    <div className="text-center py-12 space-y-4">
                        <p className="text-muted-foreground">
                            You don't have any tickets yet to the specific project.
                        </p>
                        <Button onClick={handleAddTicket}>
                            <Plus className="mr-2 h-4 w-4" />
                            Create your first ticket.
                        </Button>
                    </div>
                )}
            </div>
        </>
    )
}

export default TicketsPage;