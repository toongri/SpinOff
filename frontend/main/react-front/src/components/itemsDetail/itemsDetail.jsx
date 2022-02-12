import React from "react";
import "./itemsDetail.scss";
import { useNavigate, Link } from "react-router-dom";
import {AiOutlineUser} from 'react-icons/ai';

const ItemsDetail = ({ item, key }) => {
  let navigate = useNavigate();

  return (
    <>
    <div className="item">
    <Link to="/pin" style={{ textDecoration: 'none' }} state = {{imgUrl: item}}>
    
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
    </Link>
    </div>
    <div className="movie-info">

    </div>
    </>
  );
};

export default ItemsDetail;
