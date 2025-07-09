import {useEffect, useRef} from "react";
import {useNavigate, useSearchParams, Navigate} from "react-router";
import usePageTitle from "@/hooks/usePageTitle.tsx";
import {Loader2} from "lucide-react";
import {toast} from "sonner";
import {verifyAccount} from "@/api/auth.ts";


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
        return <Navigate to="/login" replace />;
    }

    return (
        <div className="flex items-center justify-center min-h-[60vh]">
            <div className="flex flex-col items-center space-y-4 text-center">
                <Loader2 className="h-6 w-6 animate-spin text-muted-foreground" />
                <h2 className="text-lg font-semibold text-foreground">Verifying your account...</h2>
                <p className="text-sm text-muted-foreground">
                    Please wait while we confirm your email.
                </p>
            </div>
        </div>
    );
};

export default VerifyAccountPage;