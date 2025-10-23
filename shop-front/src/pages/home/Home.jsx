import React from 'react';
import Header from '../../components/header/header';
import MainBanner from '../../components/home/MainBanner';
import Footer from '../../components/footer/Footer';
import ProductSection from '../../components/home/ProductSection';

function Home(props) {
    return (
        <>
          <Header/>
            <main>
                <MainBanner/>
                <ProductSection/>
            </main>
            <Footer/>  
        </>
    );
}

export default Home;