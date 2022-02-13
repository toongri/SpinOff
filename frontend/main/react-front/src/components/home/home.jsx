import React from 'react';
import Main from '../main/main';
import Header from '../header/header.jsx';
import {useEffect, useState} from 'react';
import Search from "../search/search";
import {createStore} from 'redux';
import './home.scss';

const Home = () => {
    const [alarmPopup, setAlarmPopup] = useState(false);
    const [dmPopup, setDmPopup] = useState(false);
    const [popup, setPopup] = useState(false);


    return (
        <>
            <Header 
              dmPopup= {dmPopup} 
              setDmPopup={setDmPopup} 
              setAlarmPopup = {setAlarmPopup}
              
            />
            {/* <div className={popup ? 'popup' : ''}> */}
            <Search
                setPopup ={setPopup}
                popup = {popup}
            />  
            <Main
                dmPopup = {dmPopup}
                alarmPopup = {alarmPopup}
            />    
            {/* </div> */}
        </>
    );
};

export default Home;