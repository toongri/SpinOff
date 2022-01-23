import React from "react";
import "./itemsDetail.scss";
import { useNavigate } from "react-router-dom";

const ItemsDetail = ({ item, key }) => {
  let navigate = useNavigate();

  return (
    <div 
      className="item"
      onClick={() => {
        navigate('/pin');
      }}
    >
      <div className="content">
        <div className="content-overay"></div>
        <img src={item} className="content-image"/>
        <div className="content-details fadeIn-bottom">
          <h3 className="content-title">title</h3>
          <p className="content-text">description</p>
        </div>
      </div>
    </div>
  );
};

export default ItemsDetail;
