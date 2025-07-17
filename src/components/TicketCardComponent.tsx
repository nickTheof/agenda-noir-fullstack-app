import {Card, CardContent, CardFooter, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {Badge} from "@/components/ui/badge";
import type { Ticket } from "@/core/types.ts"
import {Button} from "@/components/ui/button.tsx";
import {useNavigate} from "react-router";

function getPriorityVariant(priority: string) {
    switch (priority.toLowerCase()) {
        case 'high':
            return 'destructive';
        case 'medium':
            return 'default';
        case 'low':
            return 'secondary';
        default:
            return 'destructive';
    }
}

function getStatusVariant(status: string) {
    switch (status.toLowerCase()) {
        case 'open':
            return 'outline';
        case 'on_going':
            return 'secondary';
        default:
            return 'default';
    }
}

const TicketCardComponent = ({
                                 uuid,
                                 title,
                                 description,
                                 priority,
                                 status,
                                 expiryDate,
                                 onDeleteClick,
                             }: Omit<Ticket, 'id'> & {
    onDeleteClick: () => void | Promise<void>;
}) => {
    const navigate = useNavigate();
    const onEdit = () => {
       navigate(`${uuid}/update`);
    };

    return (
        <Card className="w-sm flex flex-col justify-center">
            <CardHeader onClick={onEdit} className="pb-2">
                <div className="flex justify-between items-start gap-2">
                    <CardTitle className="text-lg line-clamp-1">{title}</CardTitle>
                    <Badge variant={getPriorityVariant(priority)} className="capitalize">
                        {priority}
                    </Badge>
                </div>
            </CardHeader>

            <CardContent className="py-2">
                <p className="text-sm text-muted-foreground line-clamp-2">{description}</p>

                <div className="flex justify-between items-center pt-2">
                    <Badge variant={getStatusVariant(status)} className="capitalize">
                        {status.replace('_', ' ')}
                    </Badge>
                    <span className="text-xs text-muted-foreground">Due: {expiryDate}</span>
                </div>
            </CardContent>

            <CardFooter className="flex items-center justify-end gap-2">
                <Button variant="outline" onClick={onEdit}>
                    Edit
                </Button>
                <Button variant="destructive" onClick={onDeleteClick}>
                    Delete
                </Button>
            </CardFooter>
        </Card>
    );
};

export default TicketCardComponent;