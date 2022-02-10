import React from 'react'
import Header from "../header/header";
import Search from "../search/search";
import { Provider } from "react-redux";

export const Collection = () => {
    return (
        <>
            <Header />

            {/* <Search /> */}
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

        </>
    )
}

export default Collection;