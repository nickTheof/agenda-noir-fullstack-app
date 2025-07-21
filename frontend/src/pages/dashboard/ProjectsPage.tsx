import {useState, useEffect} from "react";
import {useLocation, useNavigate} from "react-router";
import usePageTitle from "@/hooks/usePageTitle.tsx";
import {useAuth} from "@/hooks/useAuth.tsx";
import LoaderComponent from "@/components/LoaderComponent.tsx";
import {deleteUserProject, getUserProjectsPaginated} from "@/api/projects.ts";
import ProjectCardComponent from "@/components/ProjectCardComponent.tsx";
import {toast} from "sonner";
import DeleteDialogComponent from "@/components/DeleteDialogComponent.tsx";
import {Button} from "@/components/ui/button.tsx";
import type {Project} from "@/core/types.ts";
import {Plus} from "lucide-react";
import CustomPaginationComponent, {type PaginationControlProps} from "@/components/CustomPaginationComponent.tsx";


const ProjectsPage = () => {
    usePageTitle("Projects Dashboard")
    const {accessToken, userUuid} = useAuth();
    const [projects, setProjects] = useState<Project[]>([])
    const [loading, setLoading] = useState<boolean>(true)
    const [pagination, setPagination] = useState<PaginationControlProps>({
        page: 1,
        limit: 6,
        totalItems: 0,
        totalPages: 1,
    })

    const [deleteState, setDeleteState] = useState<{
        isOpen: boolean;
        projectUuid: string | null;
    }>({
        isOpen: false,
        projectUuid: null,
    });

    const navigate = useNavigate();
    const location = useLocation()

    const handleDelete = async () => {
        if (!deleteState.projectUuid || !userUuid || !accessToken) return;
        try {
            await deleteUserProject(userUuid, deleteState.projectUuid, accessToken);
            toast.success("Project deleted successfully.");
            setProjects(prevState =>
                prevState.filter(project => project.uuid != deleteState.projectUuid)
            )
        }
        catch (err) {
            toast.error(err instanceof Error ? err.message : "Project deleted failed");
        } finally {
            setDeleteState({ isOpen: false, projectUuid: null });
        }
    };

    const handleAddProject = () => {
        navigate("../new");
    }

    useEffect(() => {
        if (!userUuid || !accessToken) return;
        getUserProjectsPaginated(userUuid, accessToken, pagination.page, pagination.limit)
            .then((data) => {
            setProjects(data.data);
            setPagination(prev => ({
                ...prev,
                total: data.totalItems,
                totalPages: data.totalPages,
            }))
        }).finally(() => setLoading(false));
    }, [userUuid, accessToken, location, pagination.page, pagination.limit])

    if (loading) {
        return <LoaderComponent
            title="Loading..."
            subtitle="Fetching user projects, stand by." />
    }
    return (
        <>
            <div className="w-full p-12 space-y-6">
                <div className="flex justify-center items-center space-x-32">
                    <h1 className="text-2xl font-bold">Your Projects</h1>
                    <Button onClick={handleAddProject}>
                        <Plus className="mr-2 h-4 w-4" />
                        New Project
                    </Button>
                </div>
                {projects.length > 0 ? (
                    <>
                        <div className="flex justify-center items-center px-4">
                            <ul className="flex flex-wrap justify-center gap-4">
                                {projects.map((project) => (
                                    <li key={project.uuid}>
                                        <ProjectCardComponent
                                            uuid={project.uuid}
                                            name={project.name}
                                            description={project.description}
                                            status={project.status}
                                            onDeleteClick={() => setDeleteState({isOpen: true, projectUuid: project.uuid})}
                                            setProjects={setProjects}
                                        />
                                    </li>
                                ))}
                            </ul>
                        </div>
                        {projects.length > 0 && pagination.totalPages > 1 && (
                           <CustomPaginationComponent pagination={pagination} setPagination={setPagination} />
                        )}
                        <DeleteDialogComponent
                            open={deleteState.isOpen}
                            onOpenChange={(open) => setDeleteState(prev => ({
                                ...prev,
                                isOpen: open,
                            }))}
                            onDelete={handleDelete}
                            entity="project"
                        />
                    </>
                ) : (
                    <div className="text-center py-12 space-y-4">
                        <p className="text-muted-foreground">
                            You don't have any projects yet.
                        </p>
                        <Button onClick={handleAddProject}>
                            <Plus className="mr-2 h-4 w-4" />
                            Create your first project
                        </Button>
                    </div>
                )}
            </div>
        </>
    )
}

export default ProjectsPage;