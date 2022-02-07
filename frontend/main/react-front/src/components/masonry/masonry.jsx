import React from 'react';
import './masonry.scss';
import ItemsDetail from "../itemsDetail/itemsDetail";
import { Button } from "react-bootstrap";
import { BsPencilFill } from "react-icons/bs";
import { useNavigate } from 'react-router-dom';
import { useSelector } from 'react-redux';

const Masonry = ({lastItemElement}) => {
    let navigate = useNavigate();
    const items = useSelector((state) => {
        console.log(state.items)
        return state.items
    })

    return (
       <>
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
        </>
    );
};

export default Masonry;