import '../../assets/css/Layout.css'
import { Outlet } from 'react-router';
import Header from '../../components/header/Header';
import Footer from '../../components/footer/Footer';

function Layout(props) {
    return (
        <div className='layout-container'>
            <Header/>
                <main>
                    <Outlet/>
                </main>
            <Footer/>
        </div>
    );
}

export default Layout;