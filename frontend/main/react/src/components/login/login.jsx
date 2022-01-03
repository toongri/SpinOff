import React from 'react';
import styles from './login.module.css';

const Login = () => {
  return (
    <>
         <div className = {styles.login__bar}>
            <ul>
              <li>로그인</li>
              <li><div></div></li>
               <li><div></div></li>
              <li><div></div></li>
            </ul>
          </div>        
    </>
  );
};

export default Login