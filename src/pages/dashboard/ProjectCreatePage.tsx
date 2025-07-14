import { useNavigate } from "react-router";
import { useForm, Controller } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useAuth } from "@/hooks/useAuth.tsx";
import usePageTitle from "@/hooks/usePageTitle.tsx";
import { createUserProject} from "@/api/projects";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Select, SelectTrigger, SelectValue, SelectContent, SelectItem } from "@/components/ui/select";
import { toast } from "sonner";
import type {ProjectFormField} from "@/core/types.ts";
import {projectCreateSchema} from "@/core/zod-schemas.ts";

const ProjectCreatePage = () => {
    usePageTitle("Create Project");
    const {
        userUuid,
        accessToken
    } = useAuth();
    const navigate = useNavigate();

    const {
        register,
        handleSubmit,
        control,
        formState: { errors, isSubmitting },
        reset
    } = useForm<ProjectFormField>({
        resolver: zodResolver(projectCreateSchema),
        defaultValues: {
            status: "OPEN"
        }
    });

    const onSubmit = async (data: ProjectFormField) => {
        if (!userUuid || !accessToken) return;
        try {
            await createUserProject(userUuid, data, accessToken);
            toast.success("Project Created successfully.");
            navigate("/dashboard/projects/view");
        } catch (err) {
            toast.error(err instanceof Error ? err.message : "Create new project failed");
            reset();
        }
    }

    return (
        <Card className="w-full max-w-md mx-auto my-4">
            <CardHeader>
                <CardTitle>Create a new project</CardTitle>
                <CardDescription>
                    Fill the below form with information about the new project.
                </CardDescription>
            </CardHeader>
            <CardContent>
                <form onSubmit={handleSubmit(onSubmit)}>
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
                            {isSubmitting ? 'Submitting...' : 'Submit'}
                        </Button>

                        <Button
                            onClick={() => navigate("../view")}
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
    );
};

export default ProjectCreatePage;