import React from 'react';
import './pinDetail.scss'
import Header from '../header/header';
import Masonry from '../masonry/masonry';

const PinDetail = () =>{
    return (
        <>
            <Header />
            <div className='banner-box'></div>
            <div className='movie-info-container'>
                <div className='movie-container'>

                <div className = "image-container">
                    <img src = "https://movie-phinf.pstatic.net/20120329_250/1332987174058pwlne_JPEG/movie_image.jpg" />
                </div>

                <div className='info-container'>
                    <ul>
                        <li className='title'>
                            <p>그랜드 부다페스트 호텔</p>
                            <div><a href = "">#</a></div>
                        </li>
                        <li className='follow-btn'>
                            <button>팔로우</button>
                        </li>
                    </ul>
                    <div className='movie-infos'>
                        <div className='info-container'>
                            <span>독일, 영국</span>
                            <span>미스터리, 모험</span>
                            <span>감독 웨스 앤더스</span>
                        </div>
                        <p>랄프 파인즈(M.구스타브), 틸다 스윈튼(마담 D), 토니 레볼로리(제로), 시얼샤 로넌(아가사)</p>
                    </div>
                    <div className='curation-box'>
                        <div className='curator same-box'>
                            <p>이 영화의 큐레이터</p>
                            <span></span>
                            <span></span>
                            <span></span>
                        </div>
                        <div className='curation same-box'>
                            <p>이 영화의 큐레이션</p>
                            <span>23</span>
                        </div>
                        <div className='collection same-box'>
                            <p>컬렉션 저장</p>
                            <span>23</span>
                        </div>
                    </div>
                    
                </div>
                </div>
              
            </div>
          
        </>
    )
}

export default PinDetail;