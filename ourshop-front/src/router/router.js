import { createBrowserRouter } from "react-router";
import { authStore } from "../store/authStore";

// 기본 레이아웃
import Layout from "../pages/layout/Layout";
import MyPageLayout from "../pages/mypage/MyPageLayout";

// 공용 페이지
import Home from "../pages/home/Home";
import Login from "../pages/auth/Login";
import BuyerRegister from "../pages/auth/BuyerRegister";
import SellerRegister from "../pages/auth/SellerRegister";
import ProductList from "../pages/product/ProductList";
import ProductDetail from "../pages/product/ProductDetail";
import Cart from "../pages/cart/Cart";
import Order from "../pages/order/Order";
import OrderComplete from "../pages/order/OrderComplete";
import ShopInfo from "../pages/shop/ShopInfo";

// Buyer 전용
import BuyerInfo from "../pages/buyer/BuyerInfo";
import BuyerShippingDetail from "../pages/buyer/BuyerShippingDetail";
import BuyerReview from "../pages/buyer/BuyerReview";
import BuyerReviewUpload from "../pages/buyer/BuyerReviewUpload";
import BuyerProductLiked from "../pages/buyer/BuyerProductLiked";

// Seller 전용
import SellerInfo from "../pages/seller/SellerInfo";
import SellerProduct from "../pages/seller/SellerProduct";
import ProductUpload from "../pages/seller/ProductUpload";

// Admin
import AdminHome from "../pages/admin/AdminHome";
import AdminUserManage from "../pages/admin/AdminUserManage";
import SellerShippingDetail from "../pages/seller/SellerShippingDetail";
import AdminProductManage from "../pages/admin/AdminProductManage";
import Faq from "../pages/faq/FAQ";
import UploadFaq from "../pages/faq/UploadFaq";
import ShopList from "../pages/shop/ShopList";
import QnA from "../pages/QnA/QnA";
import UploadQnA from "../pages/QnA/UploadQnA";
import QnADetail from "../pages/QnA/QnADetail";
import AdminQnAManage from "../pages/admin/AdminQnAManage";
import BestShopList from "../pages/shop/BestShopList";


export const router = createBrowserRouter([
  {
    path: "/",
    Component: Layout,
    children: [
      { index: true, Component: Home },
      { path: "/login", Component: Login },
      { path: "/register/buyer", Component: BuyerRegister },
      { path: "/register/seller", Component: SellerRegister },
      { path: "products", Component: ProductList },
      { path: "product/:productId", Component: ProductDetail },
      { path: "cart", Component: Cart },
      { path: "order", Component: Order },
      { path: "order/complete", Component: OrderComplete },
      { path: "shop/list", Component: ShopList },
      { path: "shop/best", Component: BestShopList },
      { path: "shop", Component: ShopInfo },
      { path: "faq", Component: Faq },
      { path: "qna", Component: QnA },
      { path: "/:mode/qna/:inquiryId", Component: QnADetail },
      { path: "/:mode/qna/upload", Component: UploadQnA },
      { path: "/:mode/qna/update/:inquiryId", Component: UploadQnA },
      {
        path: "buyer/mypage",
        Component: MyPageLayout,
        children: [
          { path: "info", Component: BuyerInfo },
          { path: "shipping", Component: BuyerShippingDetail },
          { path: "review", Component: BuyerReview },
          { path: "review/upload", Component: BuyerReviewUpload },
          { path: "review/upload/:reviewId", Component: BuyerReviewUpload },
          { path: "likes", Component: BuyerProductLiked },
        ],
      },
      {
        path: "seller/mypage",
        Component: MyPageLayout,
        children: [
          { path: "info", Component: SellerInfo },
          { path: "products", Component: SellerProduct },
          { path: "products/upload", Component: ProductUpload },
          { path: "products/:productId", Component: ProductUpload },
          { path: "shipping", Component: SellerShippingDetail },
        ],
      }
    ],
  },
  { path: "admin", Component: AdminHome,
        children: [
          { index: true, Component: AdminUserManage },
          { path: "products", Component:AdminProductManage},
          { path: "faq", Component:Faq},
          { path: "faq/upload/", Component:UploadFaq},
          { path: "faq/upload/:faqId", Component:UploadFaq},
          { path: "qna", Component:AdminQnAManage},

        ],
  },
  
]);
