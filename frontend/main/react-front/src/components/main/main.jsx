import React, { useEffect } from "react";
import ItemsDetail from "../itemsDetail/itemsDetail";
import { Button } from "react-bootstrap";
import "./main.scss";
import { makeStyles } from "@mui/styles";
import { BsPencilFill } from "react-icons/bs";
import { useNavigate } from "react-router-dom";
import Masonry from "react-masonry-css";
import ControlledCarousel from "../controlledCarousel/controlledCarousel";
import Slider from '../slider/Slider.jsx';
import ToggleButton from './toggleButton'

const useStyle = makeStyles({
  btn: {
    color: "black",
  },
});

const breakpointColumnsObj = {
  default: 5,
  1500: 4,
  1100: 3,
  700: 2,
  500: 1,
};

const Main = ({ onSearch, items }) => {
  
  let navigate = useNavigate();

  const sample = [
    "https://cdn.pixabay.com/photo/2020/09/02/20/52/dock-5539524__340.jpg",
    "https://cdn.pixabay.com/photo/2021/02/03/13/54/cupcake-5978060__340.jpg",
    "https://cdn.pixabay.com/photo/2020/05/25/20/14/holland-iris-5220407__340.jpg",
    "https://cdn.pixabay.com/photo/2020/10/08/17/39/waves-5638587__340.jpg",
    "https://cdn.pixabay.com/photo/2019/01/30/11/17/zebra-3964360__340.jpg",
    "https://cdn.pixabay.com/photo/2021/02/01/13/37/cars-5970663__340.png",
    "https://cdn.pixabay.com/photo/2019/06/05/10/34/mimosa-4253396__340.jpg",
    "https://cdn.pixabay.com/photo/2020/08/04/14/42/sky-5463015__340.jpg",
    "https://cdn.pixabay.com/photo/2021/02/03/13/54/cupcake-5978060__340.jpg",
    "https://cdn.pixabay.com/photo/2020/01/09/01/00/the-eye-on-the-greek-4751572__340.png",
    "https://cdn.pixabay.com/photo/2021/01/30/12/19/couple-5963678__340.png",
    "https://cdn.pixabay.com/photo/2021/01/23/07/53/dogs-5941898__340.jpg",
    "https://cdn.pixabay.com/photo/2020/06/15/01/06/sunset-5299957__340.jpg",
  ];

  return (
    <>
      <div className="main-container">
      
        <div className="img-container">
          <div className = "controlledCarousel-container">
            <ControlledCarousel></ControlledCarousel>
          </div>

          <div className="toggle-switch-container">
            <span className = "following-container">팔로잉</span>
            <ToggleButton />
            <span className = "found-container">발견</span>
          </div>

        </div>
      
        
        <div className="container">
          <div className="masonry-container">
              {
                sample.map((item, index) => {
                  return (
                    <ItemsDetail
                      style={{
                        width: "278px",
                      }}
                      key={index}
                      item={item}
                    />
                );
              })}
          </div>
          <div className="buttonGroup">
          <div className="buttonBox1">
              <Button
                onClick={() => {
                  navigate("/pin-build");
                }}
                variant="secondary"
                style={{
                  color: "#000",
                  backgroundColor: "black",
                  borderRadius: "50%",
                  height: "50px",
                  border: "none",
                  boxShadow: " 0px 0px 18px -10px #404040",
                  padding: "10px 10px",
                }}
              >
                <BsPencilFill
                  style={{
                    color: "yellow",
                  }}
                  size="27"
                ></BsPencilFill>
              </Button>
              </div>
              <div className="buttonBox2">
              <Button
                onClick={() => {
                  navigate("/pin-build");
                }}
                variant="secondary"
                style={{
                  color: "#000",
                  backgroundColor: "black",
                  borderRadius: "50%",
                  height: "50px",
                  border: "none",
                  boxShadow: " 0px 0px 18px -10px #404040",
                  color: "yellow",
                  fontSize: "1.1rem",
                  padding: "10px 5px",
                }}
              >
                HELP
              </Button>
              </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default Main;
