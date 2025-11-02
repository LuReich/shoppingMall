import { createBrowserRouter } from "react-router";
import Layout from "../pages/layout/Layout";
import Home from "../pages/home/Home";
import Login from "../pages/auth/Login";
import ProductList from "../pages/product/ProductList";
import ProductDetail from "../pages/product/ProductDetail";
import Cart from "../pages/cart/Cart";
import Order from "../pages/order/Order";
import BuyerRegister from "../pages/auth/BuyerRegister";
import SellerRegister from "../pages/auth/SellerRegister";
import OrderComplete from "../pages/order/OrderComplete";
import BuyerInfo from "../pages/buyer/BuyerInfo";
import BuyerShippingDetail from "../pages/buyer/BuyerShippingDetail";
import AdminHome from "../pages/admin/AdminHome";
import AdminUserManage from "../pages/admin/AdminUserManage";
import SellerInfo from "../pages/seller/SellerInfo";
import BuyerReview from "../pages/buyer/BuyerReview";
import BuyerReviewUpload from "../pages/buyer/BuyerReviewUpload";
import ProductUpload from "../pages/seller/ProductUpload";
import SellerProduct from "../pages/seller/SellerProduct";

export const router = createBrowserRouter([


    {
        path:'/',
        Component: Layout,
        children: [
            {index: true, Component: Home},
            {
                path:'products',
                Component: ProductList

            },
            {
                path: "product/:productId",
                Component: ProductDetail,
            },
            {
                path: "cart",
                Component: Cart,
            },
            {
                path: "order",
                Component: Order,
            },
            {
                path: "order/complete",
                Component: OrderComplete,
            },

            {
                path: "buyer/mypage/info",
                Component: BuyerInfo,
            },
            {
                path: "buyer/mypage/shipping",
                Component: BuyerShippingDetail,
            },
            {
                path: "buyer/mypage/review",
                Component: BuyerReview,
            },
            {
                path: "/buyer/mypage/review/upload",
                Component: BuyerReviewUpload,
            },
            {
                path: "/buyer/mypage/review/upload/:reviewId",
                Component: BuyerReviewUpload,
            },
            {
                path: "seller/mypage/info",
                Component: SellerInfo,
            },
            {
                path: "/seller/mypage/products/upload",
                Component: ProductUpload,
            },
            {
                path: "/seller/mypage/products",
                Component: SellerProduct,
            },
            {
                path: "/seller/mypage/products/:productId",
                Component: ProductUpload,
            }

        ]
        
    },

    {
        path: "admin",
        Component: AdminHome,
        children: [
            {index: true, Component: AdminUserManage},
        ]
    },

    {
        path:'/login',
        Component: Login
    },
    {
        path:'/register/buyer',
        Component: BuyerRegister
    },
    {
        path:'/register/seller',
        Component: SellerRegister
    }



]);