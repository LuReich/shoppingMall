import React from 'react';
import { Link } from 'react-router-dom';
import '../../assets/css/CSDropDown.css';

function CSDropDown(props) {
    return (
        <div className='cs-drop-down-container'>
            <Link to="/faq" className="cs-link">FAQ</Link>
            <Link to="/qna" className="cs-link">Q&A</Link>
        </div>
    );
}

export default CSDropDown;