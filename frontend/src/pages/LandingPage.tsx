import usePageTitle from "@/hooks/usePageTitle.tsx";
import {Link} from "react-router";
import {Button} from "@/components/ui/button.tsx";

const LandingPage = () => {
    usePageTitle("Welcome to Agenda Noir");

    return (
        <>
            <div className="flex flex-col items-center justify-center text-center min-h-[70vh] px-4">
                <img
                    src="/agenda-noir-logo.png"
                    alt="Agenda Noir logo"
                    className="size-16 mb-6"
                />

                <h1 className="text-3xl md:text-4xl font-serif font-bold text-neutral-800 dark:text-neutral-100 mb-2">
                    Welcome to Agenda Noir
                </h1>

                <p className="text-neutral-600 dark:text-neutral-400 mb-6 text-base md:text-lg max-w-md">
                    Your elegant, minimal project management companion.
                </p>

                <div className="flex items-center justify-center">
                    <Link to="/dashboard/projects/view">
                        <Button variant="default" size="lg">
                            Continue
                        </Button>
                    </Link>
                </div>
            </div>
        </>
    )
}

export default LandingPage;