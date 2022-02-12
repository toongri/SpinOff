import React from 'react';
import './masonry.scss';
import ItemsDetail from "../itemsDetail/itemsDetail";
import { Button } from "react-bootstrap";
import { BsPencilFill } from "react-icons/bs";
import { useNavigate } from 'react-router-dom';
import { useSelector } from 'react-redux';

const Masonry = ({lastItemElement}) => {
    let navigate = useNavigate();

    const items = useSelector((state) =>{
      console.log(state.items);
      return state.items;
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
            <button
              onClick={() => {
                navigate("/pin-build");
              }}
              variant="secondary"
              
            >
              <BsPencilFill
                style={{
                  color: "#f9cf00",
                }}
                size = "42"
              ></BsPencilFill>
            </button>
            </div>
            <div className="buttonBox2">
            <button
              onClick={() => {
                navigate("/pin-build");
              }}
              variant="secondary"
            >
              HELP
            </button>
            </div>
        </div>
        </>
    );
};

export default Masonry;