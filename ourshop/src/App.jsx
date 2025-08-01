import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Header from './components/Header/Header';
import Footer from './components/Footer/Footer';
import Home from './pages/Home';
import ProductDetail from './pages/ProductDetail';
import ProductList from './pages/ProductList'; // ProductList 임포트
import './assets/css/App.css';

function App() {
  return (
    <Router>
      <Header />
      <main className="main-content">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/product/:id" element={<ProductDetail />} />
          {/* 카테고리별 상품 목록 페이지 라우트 추가 */}
          <Route path="/products/:categoryId" element={<ProductList />} />
        </Routes>
      </main>
      <Footer />
    </Router>
  );
}

export default App;