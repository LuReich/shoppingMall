import React from 'react';
import AdminHeader from '../../components/admin/AdminHeader';
import { Outlet } from 'react-router';
import '../../assets/css/AdminHome.css';
import Footer from '../../components/footer/Footer';

function AdminHome(props) {
    return (
        <div className='admin-home-container'>
                <header className='admin-h'>
                    <AdminHeader/>
                </header>
                <section className='admin-outlet'>
                    <Outlet/>
                </section>
                <Footer/>
            </div>
    );
}

export default AdminHome;