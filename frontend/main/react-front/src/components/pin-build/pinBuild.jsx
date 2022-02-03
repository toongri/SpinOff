import React, { useEffect, useRef, useState } from "react";
import Header from "../header/header";
import "./pin-build.scss";
import ButtonGroup from "react-bootstrap/ButtonGroup";
import Button from "react-bootstrap/Button";
import { FiSend } from "react-icons/fi";
import { FiExternalLink } from "react-icons/fi";
import { AiOutlineBook } from "react-icons/ai";
import { BsFillArrowUpCircleFill } from "react-icons/bs";
import axios from "axios";
import FileUpload from './fileUpload.jsx';
import {AiFillFolderAdd} from 'react-icons/ai'
import {useNavigate} from 'react-router-dom'
import PageReload from './pageReload';

const PinBuild = () => {

  let navigate = useNavigate();
  const [title, setTitle] = useState('');
  const [content, setContent] = useState("");
  const [files, setFiles] = useState('');
  const [fileImages, setFileImages] = useState([]);
  const [formpage, setFormpage] = useState(false);
  
  const titleRef = useRef();
  const contentRef = useRef();

 const submit = () =>{
   titleRef.current.value = "";
   contentRef.current.value = "";

    axios.post(`http://localhost:8080/api/test/9/post`, {
      "collectionIds": [
      0
      ],
      "content": content,
      "hashtagContents": [
        "fdasf"
      ],
      "mediaUrls": [
        "fasdf"
      ],
      "memberId": 9,
      "movieIds": [
        0
      ],
      "publicOfPostStatus": "PUBLIC",
      "title": title
    })
    .then((res) =>{
     console.log(res.data)
     console.log('success')
    })
    .catch((err) =>{
      console.log('error')
    })
      
      setTitle('');
      setContent('');
      setFileImages([]);
      setFiles('');  
      
    console.log('content: ', content, 'title', title ,'files',files)
  }

    // 파일 저장
  const saveFileImage = (file) => {
    const item = file[0].name;
   
    setFiles(URL.createObjectURL(file[0]));
      const imgUrl = JSON.stringify(URL.createObjectURL(file[0]))
      fileImages.push(imgUrl)
      console.log(fileImages)
  };

  // 파일 삭제
  const deleteFileImage = () => {
    URL.revokeObjectURL(files);
    setFiles("");
  };

  const onChangeTitle = (e) => {
    setTitle(e.target.value);
  }

  const onChangeContent = (e) => {
    console.log(e.target.value)
    console.log(files)
    setContent(e.target.value);
  }

  const renderCondition = formpage ? <PageReload setFormpage = {setFormpage} files = {files} saveFileImage = {saveFileImage}/> : <FileUpload saveFileImage = {saveFileImage} files = {files}/>;

  useEffect(() =>{
    
  }, [renderCondition])

  return (
    <div className="navbarContainer">
      <Header></Header>
        <div className="pin_container">
          <div className="file-upload-container">
           {renderCondition}
          </div>
          
          <div className="writing-info-container">
            <form className="form">
              <div className="form-group">
                <div>
                  <select>
                    <option>테마이름</option>
                  </select>
                </div>
                <input
                  type="text"
                  className="form-control form-title"
                  placeholder="제목"
                  ref = {titleRef}
                  onChange={onChangeTitle}
                />
                <textarea
                  className="form-control form-text"
                  placeholder="내용을 입력하세요"
                  onChange={onChangeContent}
                  ref= {contentRef}
                ></textarea>
              </div>
            </form>
          <div className="function-container">
            {/**function*/}
            <div className="function">
              <a className="pinterest-link" href="https://www.pinterest.co.kr/">
               링크추가하기
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
        {/* plus screen */}
      {
        fileImages && (
        <div className = "fileImages-container">
        {
          files && (
          <div className = "imageAdd-container">
            <div 
            className = "imageAdd-btn" 
            onClick = {() => setFormpage(true)}>
                <AiFillFolderAdd size = "24"></AiFillFolderAdd>
            </div>
          </div>
          )
        } 
          {fileImages.map((data) => (
            <div className = "data-container">
              <img src = {data} />
            </div>
          ))}
        </div>
        )
      }
        <div className = "complete-btn-container">
        <button 
          onClick={submit}
        className="complete-btn">작성 완료</button>
      </div>
      </div>
    </div>
  );
};

export default PinBuild;
