import '../../assets/css/Layout.css'
import { Outlet } from 'react-router';

function Layout(props) {
    return (
        <div className='layout-container'>
            <Outlet/>
        </div>
    );
}

export default Layout;