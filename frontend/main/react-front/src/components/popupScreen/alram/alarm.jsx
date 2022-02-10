import React from 'react';
import './alarm.scss';

const Alarm = () => {
  return (
        <div className="alarmPopup-container">
            <div className="alarmPopup-info">
              <p className="alarm">알림</p>
              {/**update-info*/}
              <div className="update-info">
                <p className="update-message">
                  회원님이 팔로우하는 <strong>일금천</strong>님의 리뷰가
                  업데이트 되었습니다.
                </p>

                <div className="update-movie-container">
                  <div className="update-movie">
                    <img
                      src="https://movie-phinf.pstatic.net/20120329_250/1332987174058pwlne_JPEG/movie_image.jpg"
                      alt=""
                    />
                  </div>
                  <div className="alarm-movie-info">
                    <h5 className="title">
                      <strong>티파니에서 아침을</strong>
                    </h5>
                    <span>감독이름 표시</span>
                  </div>
                </div>
                <div className="update-movie-container">
                  <div className="update-movie">
                    <img
                      src="https://movie-phinf.pstatic.net/20161014_50/147640824152266AVn_JPEG/movie_image.jpg"
                      alt=""
                    />
                  </div>
                  <div className="alarm-movie-info">
                    <h5 className="title">
                      <strong>닥터 스트레인지</strong>
                    </h5>
                    <span>감독이름 표시</span>
                  </div>
                </div>
                {/**댓글 update info*/}
                <div className="update-info">
                  <p className="update-message">
                    <strong>[스파이더맨 리뷰]</strong>에 댓글(2)가 달렸습니다.
                  </p>

                  <div className="update-movie-container">
                    <div className="update-movie">
                      <img
                        src="https://movie-phinf.pstatic.net/20120329_250/1332987174058pwlne_JPEG/movie_image.jpg"
                        alt=""
                      />
                    </div>
                    <div className="alarm-movie-info">
                      <h5 className="title">
                        <strong>티파니에서 아침을</strong>
                      </h5>
                      <span>감독이름 표시</span>
                    </div>
                  </div>
                  <div className="update-movie-container">
                    <div className="update-movie">
                      <img
                        src="https://movie-phinf.pstatic.net/20161014_50/147640824152266AVn_JPEG/movie_image.jpg"
                        alt=""
                      />
                    </div>
                    <div className="alarm-movie-info">
                      <h5 className="title">
                        <strong>닥터 스트레인지</strong>
                      </h5>
                      <span>감독이름 표시</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
  )
};

export default Alarm