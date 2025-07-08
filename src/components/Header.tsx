import {Link} from "react-router";
import ThemeMenuToggle from "@/components/ThemeMenuToggle.tsx";

//TODO: Change navbar based on Authentication-Authorization

const Header = () => {
    return (
        <header className="fixed top-0 w-full bg-neutral-50 dark:bg-neutral-950 border-b border-neutral-200 dark:border-neutral-800 z-50">
            <div className="container mx-auto px-4 py-3 flex items-center justify-between">
                <Link to="/" className="flex items-center gap-4">
                    <img
                        src="/agenda-noir-logo.png"
                        alt="Agenda Noir Logo"
                        className="size-12 rounded-full"
                    />
                    <span
                        className="text-lg md:text-xl font-serif font-bold text-neutral-700 dark:text-neutral-200 tracking-tight"
                    >
                        Agenda Noir
                    </span>
                </Link>
                <div className="flex items-center justify-between gap-4">
                    <nav className="flex items-center gap-4 text-sm md:text-base">
                        <Link
                            to="/"
                            className="text-neutral-600 dark:text-neutral-400 hover:text-black dark:hover:text-white transition-colors"
                        >
                            Sample
                        </Link>
                        <Link
                            to="/"
                            className="text-neutral-600 dark:text-neutral-400 hover:text-black dark:hover:text-white transition-colors"
                        >
                            Sample
                        </Link>
                        <Link
                            to="/"
                            className="text-neutral-600 dark:text-neutral-400 hover:text-black dark:hover:text-white transition-colors"
                        >
                            Sample
                        </Link>
                    </nav>
                    <ThemeMenuToggle />
                </div>
            </div>
        </header>
    );
};

export default Header;