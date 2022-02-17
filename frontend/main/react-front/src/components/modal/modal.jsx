import React, { useEffect, useRef, useState } from "react";
import './modal.scss'
import PageReload from './pageReload';
import ButtonGroup from "react-bootstrap/ButtonGroup";
import Button from "react-bootstrap/Button";
import { FiSend } from "react-icons/fi";
import { FiExternalLink } from "react-icons/fi";
import { AiOutlineBook } from "react-icons/ai";
import axios from "axios";
import FileUpload from './fileUpload.jsx';
import {AiFillFolderAdd} from 'react-icons/ai'

const Modal = () => {

  const [hashTags, setHashTags] = useState([])
  const [title, setTitle] = useState('');
  const [content, setContent] = useState("");
  const [files, setFiles] = useState('');
  const [fileImages, setFileImages] = useState([]);
  const [formpage, setFormpage] = useState(false);
  const [hashTagWord, setHashTagWord] = useState([]);
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
      if(e.target.value === "#"){
   
      }

      setContent(e.target.value);
    }

    const handleOnKeyPress = (e) =>{
      console.log(e.target.value)
      let letter = e.target.value;
      let array = [];
      console.log(array);
      if(letter === ' ' || letter === '\n'){
        
      }
    }

  const renderCondition = formpage ? <PageReload setFormpage = {setFormpage} files = {files} saveFileImage = {saveFileImage}/> : <FileUpload saveFileImage = {saveFileImage} files = {files}/>;
  
  useEffect(() =>{
    
  }, [renderCondition])


  return (
    <div className='modalBackground'>
     
     <div className="modalContainer">

     <div className = "curation-box">
        <p>큐레이션 하기</p>
      </div>

      <div className = "embed-box">
        <p>링크 임베드 하기</p>
      </div>
     <  div className="file-upload-container">
          {renderCondition}
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
        </div>

        <div className="writing-info-container">
            <div className="writing-wrapper">
            <form className="form">
              <div className="form-group">
                <div className="form-select-box">
                  <span className="btn-container">
                    <button
                      onClick={e =>{
                        e.preventDefault();
                      }}
                    >전체</button>
                  </span>
                  <select>
                    <option>컬렉션 이름</option>
                  </select>
                </div>
                <input
                  type="text"
                  className="form-control form-title"
                  placeholder="제목추가"
                  ref = {titleRef}
                  onChange={onChangeTitle}
                />
                <textarea
                  className="form-control form-text"
                  placeholder = "큐레이션 설명 &#13;&#10;본문에 #을 이용해 태그를 입력해보세요(최대30개)"
                  onKeyPress={handleOnKeyPress}
                  ref= {contentRef}
                ></textarea>
              </div>
            </form>
          <div className="function-container">
            {/**function*/}
            <div className="function">
              <p className="pinterest-link">링크 추가하기</p>
                <ul className="hash-tags-container">
                 {hashTagWord?.map(item => <li>{item.join('')}</li>)}
                </ul>
            </div>
            {/*movie-info*/}
            <div className="movie-container">
              <div className="img-container">
                 <img src="https://movie-phinf.pstatic.net/20211215_297/1639556766975z0641_JPEG/movie_image.jpg" alt="movie-poster" />
              </div>
              <ul className="movie-info">
                <li className="movie-title">스파이더맨</li>
                <li className="director-info">
                  <span>감독이름 : </span>
                  <span>노진구</span>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>
      <div className = "complete-btn-container">
        <button 
          onClick={submit}
        className="complete-btn">작성완료</button>
      </div>
     </div>
    </div>
  );
};

export default Modal;