import React from 'react';
import Header from '../header/header';
import './myPage.scss';
const Mypage = () => {
    return (
        <div>
             <Header />
            <div className='banner-box'></div>
                <div className='itemDetial-user-container'>
                    <div className='itemDetial-user-image'>
                        <img src="https://cdn.pixabay.com/photo/2021/03/30/08/56/woman-6136425_960_720.jpg" alt="" />
                        <div className='profile-modi'>
                        <span className='profile-modi-content'>
                            프로필 수정
                        </span>
                        </div>
                    </div>
                    {/*큐레이터 정보*/}
                    <div className='curator-info'>
                        <p className='itemDetial-name'>마블링</p>
                        <p className='itemDetial-user-message'>상메sdfasd fasdf asdfasdfsd dfasdfasdf 두줄로 가자</p>
                        <div className='curation-follwer-following'>
                              {/*큐레이션 정보*/}
                            <div className="curation-box user-box">
                                <p>큐레이션</p>
                                <span>94</span>
                            </div>
                              {/*팔로워 정보*/}
                              <div className="follower-box user-box">
                                <p>팔로워</p>
                                <span>98</span>
                            </div>
                                 {/*팔로잉 정보*/}
                                 <div className="following-box user-box">
                                <p>팔로잉</p>
                                <span>99</span>
                            </div>
                        </div>
                    </div>
                </div>
        </div>
    );
};

export default Mypage;