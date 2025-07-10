import {Loader2} from "lucide-react";

type LoaderProps = {
    title: string;
    subtitle: string;
}

const LoaderComponent = ({title, subtitle}: LoaderProps) => {
    return (
        <div className="flex items-center justify-center min-h-[60vh]">
            <div className="flex flex-col items-center space-y-4 text-center">
                <Loader2 className="h-6 w-6 animate-spin text-muted-foreground" />
                <h2 className="text-lg font-semibold text-foreground">{title}</h2>
                <p className="text-sm text-muted-foreground">
                    {subtitle}
                </p>
            </div>
        </div>
    )
}

export default LoaderComponent;