import {useParams, useNavigate} from "react-router";
import {useState, useEffect} from "react";
import {Controller, useForm} from "react-hook-form";
import {createTicket, getTicketByUuid, updateTicket} from "@/api/tickets.ts";
import {zodResolver} from "@hookform/resolvers/zod";
import {addDays, format} from "date-fns";
import {Card, CardContent, CardDescription, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {Label} from "@/components/ui/label.tsx";
import {Input} from "@/components/ui/input.tsx";
import {Textarea} from "@/components/ui/textarea.tsx";
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select.tsx";
import {Popover, PopoverContent, PopoverTrigger} from "@/components/ui/popover.tsx";
import {Button} from "@/components/ui/button.tsx";
import {CalendarIcon} from "lucide-react";
import {Calendar} from "@/components/ui/calendar.tsx";
import LoaderComponent from "@/components/LoaderComponent.tsx";
import usePageTitle from "@/hooks/usePageTitle.tsx";
import {useAuth} from "@/hooks/useAuth.tsx";
import {toast} from "sonner";
import {ticketSchema} from "@/core/zod-schemas.ts";
import type {TicketFormFields} from "@/core/types.ts";


type TicketModeProps = {
    mode: 'edit' | 'create';
}

const TicketModePage = ({mode}: TicketModeProps) => {
    usePageTitle(mode=== "create" ? "Create Ticket" : "Update Ticket");
    const {userUuid, accessToken} = useAuth();
    const { projectUuid, ticketUuid } = useParams<{ projectUuid: string, ticketUuid: string }>();
    const navigate = useNavigate();
    const isEdit = mode === "edit" || (!!ticketUuid && mode !== "create");
    const [loading, setLoading] = useState(isEdit);

    const {
        register,
        handleSubmit,
        control,
        formState: { errors, isSubmitting },
        reset
    } = useForm<TicketFormFields>({
        resolver: zodResolver(ticketSchema),
        defaultValues: {
            title: "",
            description: "",
            status: "OPEN",
            priority: "LOW",
            expiryDate: format(addDays(new Date(), 1), "yyyy-MM-dd")
        }
    })

    useEffect(() => {
        if (isEdit && ticketUuid && userUuid && accessToken && projectUuid) {
            getTicketByUuid(userUuid, projectUuid, ticketUuid, accessToken)
                .then((data) => {
                    const values = {
                        title: data.title,
                        description: data.description,
                        status: data.status,
                        priority: data.priority,
                        expiryDate: data.expiryDate,
                    }
                    reset(values);
                })
                .catch((err) => {
                console.error("Error fetching ticket:", err);
                })
                .finally(() => setLoading(false));
        }
    }, [accessToken, isEdit, projectUuid, reset, ticketUuid, userUuid]);
    
    

    const onSubmit = async(data: TicketFormFields) => {
        if (!userUuid || !accessToken || !projectUuid) return;
        try {
            if (isEdit && ticketUuid) {
                await updateTicket(userUuid, projectUuid, ticketUuid, data, accessToken);
                toast.success("Ticket updated!");
            } else {
                await createTicket(userUuid, projectUuid,data, accessToken);
                toast.success("Ticket created!");
            }
            navigate("../", {
                replace: true,
            });
        } catch (error) {
            toast.error(
                error instanceof Error ? error.message : "Something went wrong.",
            );
            reset();
        }
    }

    if (loading) {
        return <LoaderComponent
            title="Loading..."
            subtitle="Fetching project ticket details, stand by." />
    }


    return (
        <>
            <Card className="w-full max-w-md mx-auto my-4">
                <CardHeader>
                    <CardTitle>
                        {isEdit ? "Edit Ticket" : "Create New Ticket"}
                    </CardTitle>
                    <CardDescription>
                        Fill the below form with information about the ticket.
                    </CardDescription>
                </CardHeader>
                <CardContent>
                    <form onSubmit={handleSubmit(onSubmit)}>
                        <div className="flex flex-col gap-3">
                            <div className="grid gap-2">
                                <Label htmlFor="title">Title</Label>
                                <Input
                                    id="title"
                                    type="text"
                                    placeholder="Enter the title of your ticket"
                                    autoFocus
                                    {...register("title")}
                                    disabled={isSubmitting}
                                    autoComplete="off"
                                />
                                {errors.title && (
                                    <div className="text-red-500 dark:text-red-400">{errors.title.message}</div>
                                )}
                            </div>

                            <div className="grid gap-2">
                                <Label htmlFor="description">Description</Label>
                                <Textarea
                                    id="description"
                                    placeholder="Enter the description of your ticket"
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

                            <div className="grid gap-2">
                                <Label>Priority</Label>
                                <Controller
                                    name="priority"
                                    control={control}
                                    render={({ field }) => (
                                        <Select
                                            onValueChange={field.onChange}
                                            value={field.value}
                                            disabled={isSubmitting}
                                        >
                                            <SelectTrigger>
                                                <SelectValue placeholder="Select priority" />
                                            </SelectTrigger>
                                            <SelectContent>
                                                <SelectItem value="LOW">Low</SelectItem>
                                                <SelectItem value="MEDIUM">Medium</SelectItem>
                                                <SelectItem value="HIGH">High</SelectItem>
                                                <SelectItem value="CRITICAL">Critical</SelectItem>
                                            </SelectContent>
                                        </Select>
                                    )}
                                />
                                {errors.priority && (
                                    <div className="text-red-500 dark:text-red-400">{errors.priority.message}</div>
                                )}
                            </div>
                            <div className="grid gap-2">
                                <Label>Expiry Date</Label>
                                <Controller
                                    name="expiryDate"
                                    control={control}
                                    render={({ field }) => {
                                        const dateValue = new Date(field.value);

                                        return (
                                            <Popover>
                                                <PopoverTrigger asChild>
                                                    <Button variant={"outline"} className="w-full justify-start text-left font-normal">
                                                        <CalendarIcon className="mr-2 h-4 w-4" />
                                                        {dateValue ? format(dateValue, "PPP") : "Pick a date"}
                                                    </Button>
                                                </PopoverTrigger>
                                                <PopoverContent className="w-auto p-0">
                                                    <Calendar
                                                        mode="single"
                                                        selected={dateValue}
                                                        onSelect={(date) => {
                                                            if (date) {
                                                                field.onChange(format(date, "yyyy-MM-dd"));
                                                            }
                                                        }}
                                                        disabled={(date) => date < new Date()}
                                                    />
                                                </PopoverContent>
                                            </Popover>
                                        );
                                    }}
                                />
                                {errors.expiryDate && (
                                    <div className="text-red-500 dark:text-red-400">
                                        {errors.expiryDate.message}
                                    </div>
                                )}
                            </div>

                            <Button type="submit" disabled={isSubmitting} className="w-full">
                                {isSubmitting ? 'Submitting...' : 'Submit'}
                            </Button>

                            <Button
                                onClick={() => navigate(isEdit? "../../tickets": "../")}
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
        </>
    )
}

export default TicketModePage;