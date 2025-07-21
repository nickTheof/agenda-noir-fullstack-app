import {Link} from "react-router";
import usePageTitle from "@/hooks/usePageTitle.tsx";
import {Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import UserCreateFormComponent from "@/components/UserCreateFormComponent.tsx";


const RegisterUserPage = () => {
    usePageTitle("Register User");
    return (
        <>
            <Card className="w-full max-w-sm mx-auto my-4">
                <CardHeader>
                    <CardTitle>Create your account</CardTitle>
                    <CardDescription>
                        Fill the below form to register your account
                    </CardDescription>
                </CardHeader>
                <CardContent>
                   <UserCreateFormComponent mode="open" />
                </CardContent>
                <CardFooter className="flex justify-center w-full mt-4">
                    <p className="text-sm text-muted-foreground">
                        Already have an account?{' '}
                        <Link to="/auth/login" className="text-primary hover:underline underline-offset-4">
                            Login
                        </Link>
                    </p>
                </CardFooter>
            </Card>
        </>
    )
}

export default RegisterUserPage;