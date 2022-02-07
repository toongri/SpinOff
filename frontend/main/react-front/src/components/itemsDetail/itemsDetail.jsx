import React from "react";
import "./itemsDetail.scss";
import { useNavigate, Link } from "react-router-dom";
import {AiOutlineUser} from 'react-icons/ai';

const ItemsDetail = ({ item, key }) => {
  let navigate = useNavigate();

  return (
    <Link to="/pin" style={{ textDecoration: 'none' }} state = {{imgUrl: item}}>
    <div 
      className="item" 
    >
      <div className="content">
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

        </div>
        
        </div>

        <img src={item} className="content-image"/>
        
        </div>
        
        <div className = "movie-info-container">
          <p>파니 핑크와 아멜리에</p>
          <div className = "user-info-container">
            <span className = "user-info-img-container"><AiOutlineUser color = "red"></AiOutlineUser></span>
            <span className = "user-name-container">일금천</span>
          </div>
        </div>
     
    </div>
    </Link>
  );
};

export default ItemsDetail;
