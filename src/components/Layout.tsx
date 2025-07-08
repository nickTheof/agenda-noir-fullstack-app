import {Outlet} from "react-router";
import Header from "@/components/Header.tsx";
import Footer from "@/components/Footer.tsx";

const Layout = () => {
    return (
        <>
            <Header />
            <main className="container mx-auto min-h-[90vh] pt-24">
                <Outlet />
            </main>
            <Footer />
        </>
    )
}

export default Layout;