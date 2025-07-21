import usePageTitle from "@/hooks/usePageTitle.tsx";
import {Link} from "react-router";
import {Button} from "@/components/ui/button.tsx";

const NotFoundPage = () => {
    usePageTitle("Not Found Page");
    return (
        <div className="flex flex-col justify-center items-center text-center space-y-6 px-4">
            <h1 className="text-[10rem] font-extrabold tracking-widest text-foreground">
                404
            </h1>
            <p className="text-3xl text-muted-foreground">
                Page Not Found
            </p>
            <p className="text-md text-muted-foreground max-w-md">
                The page you’re looking for doesn’t seem to exist.
            </p>
            <Button asChild>
                <Link to="/">Return Home</Link>
            </Button>
        </div>
    );
}

export default NotFoundPage;