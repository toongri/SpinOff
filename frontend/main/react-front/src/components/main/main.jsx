import React, { useEffect, useRef, useCallback } from "react";
import "./main.scss";
import { useNavigate } from "react-router-dom";
import ToggleButton from "./toggleButton";
import Masonry from "../masonry/masonry";
import store from "../redux/store";
import Alarm from '../popupScreen/alram/alarm';
import Dm from '../popupScreen/Dm/Dm';

const Main = ({ alarmPopup, dmPopup }) => {
  
  let navigate = useNavigate();

  return (
  
    <>
      <div className={`main-container`}>
        {alarmPopup && (
          <Alarm></Alarm>
        )}
        {
          dmPopup && (
            <Dm></Dm>
          )
        }
        <div className="img-container">
          <div className="toggle-switch-container">
            <span className="following-container">발견</span>
            <ToggleButton />
            <span className="found-container">팔로잉</span>
          </div>
        </div>

        <div className = "container-masonry">
          <Masonry></Masonry>
       </div>
      </div>
      )
    
    </>
  );
};

export default Main;
