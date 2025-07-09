import { Navigate, Outlet } from "react-router";
import {useAuth} from "@/hooks/useAuth.tsx";


const ProtectedRoute = () => {
    const { isAuthenticated } = useAuth();

    if (!isAuthenticated) {
        return <Navigate to="/auth/login" replace />;
    }

    return <Outlet />;
};

export default ProtectedRoute;