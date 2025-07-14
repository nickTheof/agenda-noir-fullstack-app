import {useEffect, useRef} from "react";
import {useNavigate, useSearchParams, Navigate} from "react-router";
import usePageTitle from "@/hooks/usePageTitle.tsx";
import {toast} from "sonner";
import {verifyAccount} from "@/api/auth.ts";
import LoaderComponent from "@/components/LoaderComponent.tsx";


const VerifyAccountPage = () => {
    usePageTitle("Verify Account");
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const hasVerified = useRef(false);
    const token = searchParams.get("token");

    useEffect(() => {
        if (!token || hasVerified.current) return;

        hasVerified.current = true;

        verifyAccount(token)
            .then(() => toast.success('Account verified!'))
            .catch(err => toast.error(err instanceof Error ? err.message : "Verification failed"))
            .finally(() => navigate('/login', { replace: true }));
    }, [token, navigate]);

    if (!token) {
        return <Navigate to="/auth/login" replace />;
    }

    return (
        <LoaderComponent
            title="Verifying your account..."
            subtitle="Please wait while we confirm your email."
        />
    );
};

export default VerifyAccountPage;