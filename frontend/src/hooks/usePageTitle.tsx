import {useEffect} from "react";

const APP_TITLE: string = "Agenda Noire"

const usePageTitle = (pageTitle?: string): void => {
    useEffect(() => {
        document.title = pageTitle ? pageTitle + " | " + APP_TITLE : APP_TITLE;
    }, [pageTitle]);
}

export default usePageTitle;