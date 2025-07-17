import {Card, CardContent, CardFooter, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {Badge} from "@/components/ui/badge.tsx";
import {Button} from "@/components/ui/button.tsx";
import {type Dispatch, type SetStateAction, useState} from "react";
import {Controller, useForm} from "react-hook-form";
import type {Project, ProjectFormField, ProjectStatus} from "@/core/types.ts";
import {zodResolver} from "@hookform/resolvers/zod";
import {projectCreateSchema} from "@/core/zod-schemas.ts";
import {useAuth} from "@/hooks/useAuth.tsx";
import {Label} from "@/components/ui/label.tsx";
import {Input} from "@/components/ui/input.tsx";
import {Textarea} from "@/components/ui/textarea.tsx";
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select.tsx";
import {updateUserProject} from "@/api/projects.ts";
import {toast} from "sonner";
import {useNavigate} from "react-router";

type ProjectCardProps = {
    uuid: string;
    name: string;
    description: string;
    status: ProjectStatus;
    onDeleteClick: () => void | Promise<void>;
    setProjects:  Dispatch<SetStateAction<Project[]>>;
}

const STATUS_LABELS: Record<ProjectStatus, string> = {
    "OPEN": "Open",
    "ON_GOING": "On Going",
    "CLOSED": "Closed"
};

const ProjectCardComponent = ({
                                  uuid,
                                  name,
                                  description,
                                  status,
                                  onDeleteClick,
                                  setProjects,
                              }: ProjectCardProps) => {
    const {userUuid, accessToken} = useAuth();
    const [isEdit, setIsEdit] = useState(false);
    const {
        register,
        handleSubmit,
        control,
        formState: { errors, isSubmitting },
    } = useForm<ProjectFormField>({
        resolver: zodResolver(projectCreateSchema),
        defaultValues: {
            name: name,
            description: description,
            status: status
        }
    });
    const navigate = useNavigate();


    const onUpdate = async (data: ProjectFormField): Promise<void> => {
        if (!userUuid || !accessToken) return;
        try {
            const updatedProject = await updateUserProject(userUuid, uuid, data, accessToken);
            toast.success("Project Updated successfully");
            setProjects((projects) => (
                projects.map(p => {
                    if (p.uuid === uuid) {
                        return updatedProject
                    } else {
                        return p
                    }
                })
            ))
        } catch (err) {
            toast(err instanceof Error ? err.message : "Error updating project");
        } finally {
            setIsEdit(false);
        }
    }

    const onExploreProject = () => {
        navigate(`../${uuid}/tickets`)
    }

    return (
        <>
            {isEdit ? (
                <Card
                    key={uuid}
                    className="w-sm flex flex-col justify-center"
                >
                    <CardHeader>
                        <CardTitle>Update Your Project Details</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <form onSubmit={handleSubmit(onUpdate)}>
                            <div className="flex flex-col gap-3">
                                <div className="grid gap-2">
                                    <Label htmlFor="name">Name</Label>
                                    <Input
                                        id="name"
                                        type="text"
                                        placeholder="Enter the name of your project"
                                        autoFocus
                                        {...register("name")}
                                        disabled={isSubmitting}
                                        autoComplete="off"
                                    />
                                    {errors.name && (
                                        <div className="text-red-500 dark:text-red-400">{errors.name.message}</div>
                                    )}
                                </div>

                                <div className="grid gap-2">
                                    <Label htmlFor="description">Description</Label>
                                    <Textarea
                                        id="description"
                                        placeholder="Enter the description of the project"
                                        autoComplete="off"
                                        {...register("description")}
                                        disabled={isSubmitting}
                                        className="resize-none"
                                    />
                                    {errors.description && (
                                        <div className="text-red-500 dark:text-red-400">{errors.description.message}</div>
                                    )}
                                </div>

                                <div className="grid gap-2">
                                    <Label>Status</Label>
                                    <Controller
                                        name="status"
                                        control={control}
                                        render={({ field }) => (
                                            <Select
                                                onValueChange={field.onChange}
                                                value={field.value}
                                                disabled={isSubmitting}
                                            >
                                                <SelectTrigger>
                                                    <SelectValue placeholder="Select status" />
                                                </SelectTrigger>
                                                <SelectContent>
                                                    <SelectItem value="OPEN">OPEN</SelectItem>
                                                    <SelectItem value="ON_GOING">ON GOING</SelectItem>
                                                    <SelectItem value="CLOSED">CLOSED</SelectItem>
                                                </SelectContent>
                                            </Select>
                                        )}
                                    />
                                    {errors.status && (
                                        <div className="text-red-500 dark:text-red-400">{errors.status.message}</div>
                                    )}
                                </div>

                                <Button type="submit" disabled={isSubmitting} className="w-full">
                                    {isSubmitting ? 'Updating...' : 'Update'}
                                </Button>

                                <Button
                                    onClick={()=> setIsEdit(false)}
                                    type="button"
                                    disabled={isSubmitting}
                                    variant="outline"
                                    className="w-full"
                                >
                                    Cancel
                                </Button>
                            </div>
                        </form>
                    </CardContent>
                </Card>
            ):
                <Card
                    key={uuid}
                    className="w-sm h-60 flex flex-col justify-center"
                >
                    <CardHeader
                        className="flex items-center justify-between"
                    >
                        <CardTitle>{name}</CardTitle>
                        <Badge>{STATUS_LABELS[status]}</Badge>
                    </CardHeader>
                    <CardContent
                        className="text-sm text-muted-foreground"
                    >
                        {description}
                    </CardContent>
                    <CardFooter className="flex items-center justify-between gap-2">
                        <Button onClick={onExploreProject}
                        >Explore</Button>
                        <div
                            className="flex items-center justify-between gap-2"
                        >
                            <Button
                                onClick={() => setIsEdit(true)}
                                variant="outline"
                            >
                                Edit
                            </Button>
                            <Button
                                variant="destructive"
                                onClick={onDeleteClick}
                            >
                                Delete
                            </Button>
                        </div>
                    </CardFooter>
                </Card>
            }
        </>
    )
}

export default ProjectCardComponent;