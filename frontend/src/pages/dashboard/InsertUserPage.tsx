import usePageTitle from "@/hooks/usePageTitle.tsx";
import {Card, CardContent, CardFooter, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import UserCreateFormComponent from "@/components/UserCreateFormComponent.tsx";
import {Link} from "react-router";
import {Button} from "@/components/ui/button.tsx";

const InsertUserPage = () => {
    usePageTitle("Insert User");
    return (
        <>
            <Card className="w-full max-w-sm mx-auto my-4">
                <CardHeader>
                    <CardTitle>Create a new user</CardTitle>
                </CardHeader>
                <CardContent>
                    <UserCreateFormComponent mode="protected" />
                </CardContent>
                <CardFooter className="flex justify-center w-full">
                    <Button className="w-full" variant="outline" asChild>
                        <Link to="../">Back</Link>
                    </Button>
                </CardFooter>
            </Card>
        </>
    )
}

export default InsertUserPage;