import { Navigate, Outlet } from "react-router";
import {useAuth} from "@/hooks/useAuth.tsx";
import LoaderComponent from "@/components/LoaderComponent.tsx";


const ProtectedRoute = () => {
    const { isAuthenticated, loading } = useAuth();

    if (loading) {
        return <LoaderComponent
            title="Authenticating..."
            subtitle="Verifying credentials, stand by."
        />
    }

    if (!isAuthenticated) {
        return <Navigate to="/auth/login" replace />;
    }

    return <Outlet />;
};

export default ProtectedRoute;