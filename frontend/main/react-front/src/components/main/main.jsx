import React, { useEffect, useRef, useCallback } from "react";
import ItemsDetail from "../itemsDetail/itemsDetail";
import { Button } from "react-bootstrap";
import "./main.scss";
import { makeStyles } from "@mui/styles";
import { BsPencilFill } from "react-icons/bs";
import { useNavigate } from "react-router-dom";
import Masonry from "react-masonry-css";
import ControlledCarousel from "../controlledCarousel/controlledCarousel";
import Slider from '../slider/Slider.jsx';
import ToggleButton from './toggleButton';
import {useSelector, useDispatch} from 'react-redux';

const Main = () => {
  let navigate = useNavigate();
  const hasMore = useSelector(state => state.hasMore);  
  const pageNumber = useSelector(state => state.pageNumber);
  const dispatch = useDispatch();
  const loading = useSelector(state => state.loading);

  const observer = useRef();
  const lastItemElement = useCallback(node =>{
    if(loading) return
    if(observer.current) observer.current.disconnect();

    observer.current = new IntersectionObserver(entries =>{
      if(entries[0].isIntersecting && hasMore){
        dispatch({
         type: "UPDATE",
         pageNumber: pageNumber + 1
        })
      }
    }, [hasMore])=-
    if(node) observer.current.observe(node);
  })

  const query = useSelector(state => state.query)

  const items = useSelector((state) => {
      console.log(state.items)
      return state.items
  })

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
                
                items.map((item, index) => {
                  if(items.length === index + 1){
                    return <ItemsDetail
                      style={{
                        width: "278px",
                      }}
                      key={index}
                      item={item}
                      ref = {lastItemElement}
                    />
                  }else{
                  return (
                    <ItemsDetail
                      style={{
                        width: "278px",
                      }}
                      key={index}
                      item={item}
                    />
                );
                  }
              })
              
              }
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
