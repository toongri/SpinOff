import React, { useState, useEffect } from "react";
import Header from "../header/header";
import Search from "../search/search";
import "./pinAll.scss";
import{AiFillCaretLeft, AiFillCaretRight} from 'react-icons/ai'

const PinAll = () => {
  // const adress = JSON.stringify(window.location.href);
  const [address, setAddress] = useState('');

  const findaddressName = () =>{
    const addressName = window.location.href.split('/')[3];
    setAddress(addressName)
    console.log(address)
  }

  useEffect(() => {
  findaddressName();

  }, [])

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

  return (
    <>
      <Header></Header>
      <Search></Search>
      
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
            <span>sdaf</span>
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
            <img src = "https://movie-phinf.pstatic.net/20120329_250/1332987174058pwlne_JPEG/movie_image.jpg" />
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
         <div className = "right-btn-arrow arrow-btn">
            <button>
              <AiFillCaretRight size = "47"></AiFillCaretRight>
            </button>
          </div> 
      </div>
      <div>
      </div>
       
       </div>
    </>
  );
};

export default PinAll;
