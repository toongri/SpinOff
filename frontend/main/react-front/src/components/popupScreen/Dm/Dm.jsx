import React from 'react';
import './Dm.scss';
import {BsPersonCircle} from 'react-icons/bs'

const Dm = () => {
  return <>
     <div className="dm-container">
        <div className="dm-info">
            <p className="dm">DM</p>
       
        <ul className='dm-message-container'>
            <li>
                <span>
                    <BsPersonCircle className = "person-icon" size = "60" color='#f9cf00'></BsPersonCircle>
                </span>
                <div className='user-info'>
                    <h5>Username</h5>
                    <p>마지막으로 주고 받은 쪽지 내용 보여주기</p>
                </div>
            </li>
            <li>
                <span className = "person-icon">
                    <BsPersonCircle  className = "icon"size = "60" color='#f9cf00'></BsPersonCircle>
                </span>
                <div className='user-info'>
                    <h5>Username</h5>
                    <p>마지막으로 주고 받은 쪽지 내용 보여주기</p>
                </div>
            </li>
            <li>
            <span>
                    <BsPersonCircle size = "60" color='#f9cf00'></BsPersonCircle>
                </span>
                <div className='user-info'>
                    <h5>Username</h5>
                    <p>마지막으로 주고 받은 쪽지 내용 보여주기</p>
                </div>
            </li>
            <li> 
                <span>
                    <BsPersonCircle size = "60" color='#f9cf00'></BsPersonCircle>
                </span>
                <div className='user-info'>
                    <h5>Username</h5>
                    <p>마지막으로 주고 받은 쪽지 내용 보여주기</p>
                </div>
                </li>
            <li>
            <span>
                    <BsPersonCircle
                    color='#f9cf00'
                    size = "60"></BsPersonCircle>
                </span>
                <div className='user-info'>
                    <h5>Username</h5>
                    <p>마지막으로 주고 받은 쪽지 내용 보여주기</p>
                </div>
            </li>
        </ul>
        </div>
     </div>
  </>;
};

export default Dm
