import React, { useState } from "react";
import { Provider } from "react-redux";
import Header from "../header/header";
import Search from "../search/search";
import { createStore } from "redux";
import "./pinAll.scss";
import {BsFillArrowLeftCircleFill} from 'react-icons/bs'
import  {BsFillArrowRightCircleFill} from 'react-icons/bs';

const reducer = (currentState, action) => {
  if (currentState === undefined) {
    return {
      query: "",
      items: [],
      pageNumber: 1,
      hasMore: false,
      loading: true,
    };
  }

  const newState = { ...currentState };

  if (action.type === "SEARCH") {
    newState.items = action.items;
    newState.query = action.query;
    newState.loading = action.loading;
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
      <div className="container">
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

        {!!slideSpot && (
          <button onClick={handlePrevBtn} className="slideArrow arrowLeft">
           <BsFillArrowLeftCircleFill />
          </button>
        )}
        <ul className="storeImgUl">
          <div
            style={{ transform: `translateX(${slideSpot}px)` }}
            className="slideInner"
          >
            {images.map((img, i) => (
              <li key={i} className="storeImgLi">   
                <img src={img} />
              </li>
            ))}
            {slideSpot !== slideEnd && (
              <button
                onClick={handleNextBtn}
                className="slideArrow arrowRight"
              >
                <BsFillArrowRightCircleFill></BsFillArrowRightCircleFill>
              </button>
            )}
          </div>
        </ul>
      </div>
    </>
  );
};

export default PinAll;
