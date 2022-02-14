import React from "react";
import "./itemsDetail.scss";
import { useNavigate, Link } from "react-router-dom";
import {AiOutlineUser} from 'react-icons/ai';
import {BiLinkExternal} from 'react-icons/bi';
import {FiMoreHorizontal} from 'react-icons/fi';

const ItemsDetail = ({ item, key }) => {
  let navigate = useNavigate();

  return (
    <>
    <div className="item">
 
      <div className="content">
           <Link to="/pin" style={{ textDecoration: 'none' }} state = {{imgUrl: item}}>
    
        <div className="content-overay">

        <div className="content-details fadeIn-bottom">
         
         <div className = "select-box">
          <div className = "select-box-select">
            <select>
              <option>컬렉션 이름</option>
            </select>
          </div>
          <div className = "select-box-btn">
            <button>저장</button>
          </div>
         </div>

         <div className="select-icons">
            <span className="store-icons">
              <BiLinkExternal size= "26" color="#f24860"></BiLinkExternal>
            </span>
            <span className="more-icons">
              <FiMoreHorizontal size= "26"color="#f24860"></FiMoreHorizontal>
            </span>
          </div>
        </div>
         
        </div>
        </Link>
        <img src={item} className="content-image"/>
        
        </div>
        
   
    
    <div className="movie-info">
      <p>파니 핑크와 아멜리에</p>
      <div className="user-container">
      <span className="user-img">< AiOutlineUser size= '25' color="red"/></span>
      <span className="name">일금천</span>
      </div>
    </div>
    </div>
  
    </>
  );
};

export default ItemsDetail;
