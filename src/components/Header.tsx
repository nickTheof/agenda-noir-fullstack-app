import {Link, useNavigate} from "react-router";
import ThemeMenuToggle from "@/components/ThemeMenuToggle.tsx";
import {useAuth} from "@/hooks/useAuth.tsx";
import {Button} from "@/components/ui/button.tsx";

const Header = () => {
    const {isAuthenticated, username, logout} = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate("/");
    };

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
                <div className="flex items-center justify-end gap-4">
                    {isAuthenticated ? (
                        <div className="flex items-center gap-4">
              <span className="text-sm font-medium text-neutral-700 dark:text-neutral-200">
                {username}
              </span>
                            <Button
                                onClick={handleLogout}
                                variant="outline"
                                className="text-sm font-medium"
                            >
                                Logout
                            </Button>
                        </div>
                    ) : (
                        <Button
                            variant="outline"
                            className="text-sm font-medium cursor-default"
                            asChild
                        >
                            <Link to="/auth/login">Login</Link>
                        </Button>
                    )}
                    <ThemeMenuToggle />
                </div>
            </div>
        </header>
    );
};

export default Header;