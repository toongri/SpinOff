import React from 'react';
import styles from './header.module.css';
import Login from '../login/login.jsx';

const Header = () => {
  return (
    <>
     {/* header */}
        <header> 
          <nav>     
            <div className = {styles.logoContainer}>
              <a href = '/' className = {styles.logo}>
              {/* <img src="" alt="" />   */}
                LOGO
              </a>
            </div>
            <div className = {styles.sub_choice}>
             <span>도슨트</span>
             <span>스토어</span>
            </div>
            <Login />
          </nav>
        </header>
        </>
  );
};

export default Header;