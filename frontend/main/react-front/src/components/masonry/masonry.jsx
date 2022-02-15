import React from 'react';
import './masonry.scss';
import ItemsDetail from "../itemsDetail/itemsDetail";
import { Button } from "react-bootstrap";
import { BsPencilFill } from "react-icons/bs";
import { useNavigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import { useEffect } from 'react';
import Masonry from 'react-masonry-css';

const LayOut = ({lastItemElement}) => {
    let navigate = useNavigate();

    const items = useSelector((state) =>{
      console.log(state.items);
      return state.items;
    })

    // useEffect(() =>{
    //   const script = document.createElement('script');
    //   script.src = "./masonry/masonry.pkgd.min.js";
    //   console.log(script)
    //   script.async = true;
    //   document.body.appendChild(script);

    //   return () => {
    //     document.body.removeChild(script);
    //   }
    // }, [])
    const breakpointColumnsObj = {
      default: 6,
      1100: 4,
      700: 3,
      500: 2
    };
    
    return (
       <>
       <Masonry 
        breakpointCols={breakpointColumnsObj}
        className="my-masonry-grid"
        columnClassName="my-masonry-grid_column"> 
             {items.map((item, index) => {
                  return <ItemsDetail
                    style={{
                      width: "278px",
                    }}
                    key={index}
                    item={item}
                    ref = {lastItemElement}
                  />
             }
             )
            }
        </Masonry>
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
                size = "40"
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

export default LayOut;