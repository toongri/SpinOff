import React from 'react';
import Header from '../header/header';
import './pin.scss';
import { useLocation } from "react-router-dom";

const Pin = (props) => {
    const imgUrl = useLocation().state.imgUrl;
    return (
        <>      
           <Header></Header>
            {/* <Provider store = {store}>
                <Search></Search>
           </Provider> */}
           <div className="pinBox-container">
                <div className='pin-container'>

                    <div className='img-container'>
                        <img src={imgUrl} alt="" />
                    </div>

                    <div className='data-container'>
                        {/*function container*/}
                        <div className='function-container'>
                            <div className = "curator-info">
                                <span>사진</span>
                                <span>큐레이터 이름</span>
                            </div>
                            <div className = "follow-btn">
                                <button>팔로우</button>
                            </div>
                        </div>
                        
                     {/*title container*/}
                     <div className='title-container'>
                        <div className="title">이 글 한번만 읽어줘</div>
                    </div>

                          {/*description container*/}
                     <div className='description-container'>
                             <div className = "description">
                                 <p>이글을 한번만 읽어줘
                                    이글을 한번만 읽어줘
                                    이글을 한번만 읽어줘
                                    이글을 한번만 읽어줘
                                    이글을 한번만 읽어줘
                                    이글을 한번만 읽어줘
                                    이글을 한번만 읽어줘
                                    이글을 한번만 읽어줘
                                 </p>
                             </div>
                             <div className='folwer-container'>
                                   {/*description container*/}
                                 <div className = "send-container">
                                    <div className = "date">
                                        <span>2022년 xx월 xx일</span>
                                    </div>
                                    <div className = "icons">

                                    </div>
                                 </div>
                                   {/*button container*/}
                                 <div className = "link-container">
                                    <span className = "link"><a href = "#">https://www.pinterest.co.kr/</a></span>
                                    <div className = "collection-container">
                                       
                                            <select className = "select-container">
                                                <option>컬렉션 이름</option>
                                            </select>
                                        
                                        <span className = "store-btn">
                                            <button>저장</button>
                                        </span>
                                    </div>
                                 </div>
                             </div>

                             <div className='movie-container'>
                              <div className="image-container">
                                {/* <img src="https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/bb48ffc1-bf25-486b-a056-63c3bfce1223/debe70w-6b46c00e-5867-40ae-af7c-918d40bcc81a.jpg/v1/fill/w_1280,h_1897,q_75,strp/spider_man__spider_verse__2021__poster_by_bakikayaa_debe70w-fullview.jpg?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOjdlMGQxODg5ODIyNjQzNzNhNWYwZDQxNWVhMGQyNmUwIiwiaXNzIjoidXJuOmFwcDo3ZTBkMTg4OTgyMjY0MzczYTVmMGQ0MTVlYTBkMjZlMCIsIm9iaiI6W1t7ImhlaWdodCI6Ijw9MTg5NyIsInBhdGgiOiJcL2ZcL2JiNDhmZmMxLWJmMjUtNDg2Yi1hMDU2LTYzYzNiZmNlMTIyM1wvZGViZTcwdy02YjQ2YzAwZS01ODY3LTQwYWUtYWY3Yy05MThkNDBiY2M4MWEuanBnIiwid2lkdGgiOiI8PTEyODAifV1dLCJhdWQiOlsidXJuOnNlcnZpY2U6aW1hZ2Uub3BlcmF0aW9ucyJdfQ.PFMHxTD61nlG8DpkSdXwB6az9TzE3iSUBnwuaOGDzM0" alt="movie-poster" /> */}
                                    dasffd
                            </div>
                            <ul className="movie-info">
                                <li className="moivie-title">스파이더맨</li>
                                <li className="director-info">
                                    <span>감독이름 : </span>
                                    <span>노진구</span>
                                </li>
                            </ul>
                            </div>
                            <div className = "footer">
                               <ul>
                                <li>
                                    <span>d</span>
                                     <span>3,000개</span>
                                </li>
                                 <li>
                                    <span>d</span>
                                    <span>125개</span>
                                 </li>
                                  <li className = "comment-li">
                                  답글 더보기 >
                                  </li>
                               </ul>
                            </div>
                
                         </div>
                    </div>
                </div>
            </div>
        </>
    );
};

export default Pin;