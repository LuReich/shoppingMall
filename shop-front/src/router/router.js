import { createBrowserRouter } from "react-router";
import Layout from "../pages/layout/Layout";
import Home from "../pages/home/Home";
import Login from "../components/auth/Login";

export const router = createBrowserRouter([


    {
        path:'/',
        Component: Layout,
        children: [
            {index: true, Component: Home}
        ]
        
    },

    {
        path:'/login',
        Component: Login
    }


]);