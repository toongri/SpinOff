import React, { useEffect, useRef, useCallback } from "react";
import "./main.scss";
import { useNavigate } from "react-router-dom";
import ToggleButton from "./toggleButton";
import Masonry from "../masonry/masonry";
import store from "../redux/store";
import Alarm from '../popupScreen/alram/alarm';
import Dm from '../popupScreen/Dm/Dm';
import "slick-carousel/slick/slick.css"; 
import "slick-carousel/slick/slick-theme.css";
import Slider from 'react-slick';

const Main = ({ alarmPopup, dmPopup }) => {
  
  let navigate = useNavigate();
  
  const handleSecondClick = () =>{
    document.querySelector('.slide-container').style.transform = 'translate(-100vw)';
    console.log( document.querySelector('.slide-container'))
  }

  const settings = {
    dots: true,
    infinite: true,
    speed: 600,
    slidesToShow: 1,
    slidesToScroll: 1
  };

  return (
    <>
      <div className="main-container">
      <div className = "slide-container">
         <Slider {...settings}>
         <div>
            <img src="https://cdn.pixabay.com/photo/2021/10/16/05/43/love-6713977_960_720.jpg" alt="" /> 
          </div>
          <div>
          <img src="https://cdn.pixabay.com/photo/2018/09/30/10/21/woman-3713108_960_720.jpg" alt="" />
          </div>
          <div>
           <img src="https://cdn.pixabay.com/photo/2017/09/14/11/47/sky-2748735_960_720.jpg" alt="" />
          </div>
         </Slider>
        </div>
        {alarmPopup && (
          <Alarm></Alarm>
        )}
        {
          dmPopup && (
            <Dm></Dm>
          )
        }
        <div className = "recomendation-box">
          <div className="recomendation">
          <div className="description">
            <p>추천하는 도슨트 컨텐츠<br/>지금 읽어보세요</p>
          </div>
          <div className="go-box">
            <span>오늘의 도슨트 보러가기</span>
          </div>
          </div>
        </div>
        <div className="img-container">
          <div className="toggle-switch-container">
            <span className="following-container">발견</span>
            <ToggleButton />
            <span className="found-container">팔로잉</span>
          </div>
        </div>

        {/* <div className = "container-masonry"> */}
          <Masonry></Masonry>
       {/* </div> */}
      </div>
      )
    
    </>
  );
};

export default Main;
