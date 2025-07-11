import { Loader2 } from "lucide-react";
import { cn } from "@/lib/utils";

type LoaderProps = {
    title?: string;
    subtitle?: string;
    className?: string;
    spinnerClassName?: string;
    titleClassName?: string;
    subtitleClassName?: string;
};

const LoaderComponent = ({
                             title = "Loading",
                             subtitle = "Please wait while we process your request",
                             className,
                             spinnerClassName,
                             titleClassName,
                             subtitleClassName,
                         }: LoaderProps) => {
    return (
        <div className={cn(
            "flex items-center justify-center min-h-[60vh] w-full",
            className
        )}>
            <div className="flex flex-col items-center space-y-4 text-center max-w-xs">
                <Loader2
                    className={cn(
                        "h-8 w-8 animate-spin text-primary", // Using text-primary for better visibility
                        spinnerClassName
                    )}
                    aria-hidden="true"
                />
                {title && (
                    <h2 className={cn(
                        "text-lg font-semibold text-foreground",
                        titleClassName
                    )}>
                        {title}
                    </h2>
                )}
                {subtitle && (
                    <p className={cn(
                        "text-sm text-muted-foreground",
                        subtitleClassName
                    )}>
                        {subtitle}
                    </p>
                )}
            </div>
        </div>
    );
};

export default LoaderComponent;