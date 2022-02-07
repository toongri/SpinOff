import React, { useState } from "react";
import { Provider } from "react-redux";
import Header from "../header/header";
import Search from "../search/search";
import { createStore } from "redux";
import "./pinAll.scss";
import {BsFillArrowLeftCircleFill} from 'react-icons/bs';
import{AiFillCaretLeft, AiFillCaretRight} from 'react-icons/ai'
import Masonry from '../masonry/masonry';

const reducer = (currentState, action) => {
  if (currentState === undefined) {
    return {
      query: "",
      items: [],
      pageNumber: 1,
      hasMore: false,
      loading: true,
      pinAllOn: false,
      pinOn: false
    };
  }

  const newState = { ...currentState };

  if (action.type === "SEARCH") {
    newState.items = action.items;
    newState.query = action.query;
    newState.loading = action.loading;
    newState.pinAllOn = action.pinAllOn
    newState.pinOn = action.pinOn;
  }

  if (action.type === "UPDATE") {
    newState.pageNumber = action.pageNumber;
  }

  return newState;
};

const store = createStore(reducer);

const PinAll = () => {
  const [slideSpot, setSlideSpot] = useState(0);
  const SLIDE_GAP = 14; //각 슬라이드 사이 간격
  const SLIDE_MOVING_UNIT = 500; //슬라이드 버튼 클릭 시 움직일 길이
  const IMG_WIDTH = 400; //이미지 가로 길이
   const [pinAllOn, setPinAllOn] = useState(true);

  const images = [
    "https://i.picsum.photos/id/237/200/300.jpg?hmac=TmmQSbShHz9CdQm0NkEjx1Dyh_Y984R9LpNrpvH2D_U",
    "https://i.picsum.photos/id/866/200/300.jpg?hmac=rcadCENKh4rD6MAp6V_ma-AyWv641M4iiOpe1RyFHeI",
    "https://i.picsum.photos/id/300/200/300.jpg?hmac=Xi1dg4LbyPZg1QtWl3o5UaAR1CehsYO-4N8JxiSr4Vo",
    "https://i.picsum.photos/id/147/200/300.jpg?hmac=HvL1R0waHTWxScs3tF6eMlLs2JShbg25KJn03eSoqqo",
    "https://i.picsum.photos/id/237/200/300.jpg?hmac=TmmQSbShHz9CdQm0NkEjx1Dyh_Y984R9LpNrpvH2D_U",
    "https://i.picsum.photos/id/866/200/300.jpg?hmac=rcadCENKh4rD6MAp6V_ma-AyWv641M4iiOpe1RyFHeI",
    "https://i.picsum.photos/id/300/200/300.jpg?hmac=Xi1dg4LbyPZg1QtWl3o5UaAR1CehsYO-4N8JxiSr4Vo",
    "https://i.picsum.photos/id/147/200/300.jpg?hmac=HvL1R0waHTWxScs3tF6eMlLs2JShbg25KJn03eSoqqo",
    "https://i.picsum.photos/id/237/200/300.jpg?hmac=TmmQSbShHz9CdQm0NkEjx1Dyh_Y984R9LpNrpvH2D_U",
  ];

  let imgQuantity = images.length;
  let slideWidth = IMG_WIDTH + imgQuantity + (imgQuantity - 1);
  let hiddenSlideWidth = slideWidth - window.innerWidth;
  let slideEnd = 0;

  const handlePrevBtn = () => {
    if (Math.abs(slideSpot) < SLIDE_MOVING_UNIT) {
      //슬라이드 왼쪽으로 남은 값이 한 번에 이동하는 값보다 작으면
      setSlideSpot(0);
    } else {
      //그 외의 경우
      slideSpot(slideSpot + SLIDE_MOVING_UNIT);
    }
  };

  const handleNextBtn = () => {
    if (hiddenSlideWidth - Math.abs(slideSpot) < SLIDE_MOVING_UNIT) {
      setSlideSpot(slideSpot - (hiddenSlideWidth - Math.abs(slideSpot)));
      slideEnd = slideSpot - (hiddenSlideWidth - Math.abs(slideSpot));
    } else {
      //남아있는 슬라이드의 길이가 한 번에 움직여야 하는 값보다 크면
      setSlideSpot(slideSpot - SLIDE_MOVING_UNIT);
    }
  };

  return (
    <>
    
      <Header></Header>
      <Provider store={store}>
        <Search></Search>
      </Provider>
      
      <div className="pinAll-container">
        <div className="tags-container">
            <span>sdaf</span>
            <span>Hell World</span>
            <span>sdaf</span>
            <span>FDSA</span>
            <span>sdaf</span>
            <span>FASDF</span>
            <span>sdaf</span>
            <span>sdaf</span>
        </div>

        <div className = "box-container">
          <div className = "left-btn-arrow arrow-btn">
            <button>
            <div>
              <AiFillCaretLeft size = "47"></AiFillCaretLeft>
            </div>
            </button>
          </div>
          <div className = "img-container">
            <img src = "https://movie-phinf.pstatic.net/20120329_250/1332987174058pwlne_JPEG/movie_image.jpg"/>
            
            <div className = "bottomScreen-container">  
              <div className = "description-box">
                <ul>
                  <li>마블링님이 팔로우</li>
                   <li>마블 시리즈</li>
                    <li>@Alfredo Vaccarro</li>
                </ul>
              </div>

              <div className = "follow-box">
                <button>
                  팔로우
                </button>
              </div>
            </div>
          </div>
       

        <div className = "img-container">
            <img src = "https://movie-phinf.pstatic.net/20130311_165/13629835475487aDfz_JPEG/movie_image.jpg"/>
            <div className = "bottomScreen-container">
               <div className = "description-box">
                <ul>
                  <li>마블링님이 팔로우</li>
                   <li>마블 시리즈</li>
                    <li>@Alfredo Vaccarro</li>
                </ul>
              </div>

              <div className = "follow-box">
                <button>
                  팔로우
                </button>
              </div>
            </div>
        </div>
         <div className = "img-container">
            <img src = "https://movie-phinf.pstatic.net/20211215_297/1639556766975z0641_JPEG/movie_image.jpg"/>
            <div className = "bottomScreen-container">
               <div className = "description-box">
                <ul>
                  <li>마블링님이 팔로우</li>
                   <li>마블 시리즈</li>
                    <li>@Alfredo Vaccarro</li>
                </ul>
              </div>

              <div className = "follow-box">
                <button>
                  팔로우
                </button>
              </div>
            </div>
        </div>
         <div className = "right-btn-arrow arrow-btn">
            <button>
              <AiFillCaretRight size = "47"></AiFillCaretRight>
            </button>
          </div> 
      </div>
     
       
       </div>
    </>
  );
};

export default PinAll;
