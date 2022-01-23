import React from "react";
import Header from "../header/header";
import "./pin-build.scss";
import ButtonGroup from "react-bootstrap/ButtonGroup";
import Button from "react-bootstrap/Button";
import { FiSend } from "react-icons/fi";
import { FiExternalLink } from "react-icons/fi";
import { AiOutlineBook } from "react-icons/ai";
import {BsFillArrowUpCircleFill} from 'react-icons/bs'

const PinBuild = () => {
  return (
    <div className="navbarContainer">
      <Header></Header>
      <div className="pin_container">
        <div className="file-upload-container">
          <input type="file" id="file" name="file" multiple />
          <div class="drag-text">
              <div><BsFillArrowUpCircleFill size = "30"></BsFillArrowUpCircleFill></div>
            <p>드래그하거나 클릭하여 업로드</p>
          </div>
        </div>
        <div className="writing-info-container">
          <form className="form">
            <div className="form-group">
              <input
                type="text"
                className="form-control form-title"
                placeholder="제목"
              />
              <textarea
                className="form-control form-text"
                placeholder="내용을 입력하세요"
              ></textarea>
            </div>
          </form>
          
          <div className="function-container">
            {/**function*/}
            <div className="function">
              <a className="pinterest-link" href="https://www.pinterest.co.kr/">
                www.pinterest.co.kr
              </a>
              {/*function-buttons*/}
              <div className="function-buttons">
                <ButtonGroup>
                  <Button className="button">
                    <FiSend></FiSend>
                  </Button>
                  <Button className="button">
                    <FiExternalLink></FiExternalLink>
                  </Button>
                  <Button className="button">
                    <AiOutlineBook></AiOutlineBook>
                  </Button>
                </ButtonGroup>
              </div>
            </div>
            {/*movie-info*/}
            <div className="movie-container">
              <div className="img-container">
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
          </div>
        </div>
      </div>
    </div>
  );
};

export default PinBuild;
