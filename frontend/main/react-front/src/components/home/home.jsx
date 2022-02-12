import React from 'react';
import Main from '../main/main';
import Header from '../header/header.jsx';
import {useEffect, useState} from 'react';
import Search from "../search/search";
import {createStore} from 'redux';

const Home = () => {
    const [alarmPopup, setAlarmPopup] = useState(false);
    const [dmPopup, setDmPopup] = useState(false);

    return (
        <>
            <Header 
              dmPopup= {dmPopup} 
              setDmPopup={setDmPopup} 
              setAlarmPopup = {setAlarmPopup}
            />
            <Search/>  
            <Main
             dmPopup = {dmPopup}
             alarmPopup = {alarmPopup}/>    
        </>
    );
};

export default Home;