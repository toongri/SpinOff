import React, {useState} from "react";
import "./header.scss";
import { FiSend } from "react-icons/fi";
import { IoPersonCircleOutline } from "react-icons/io5";
import { AiOutlineBell } from "react-icons/ai";
import {ButtonGroup} from "react-bootstrap";
import logoImg from './images/asset.png';
import { useNavigate } from "react-router-dom";

const buttonStyle = {
  position: "relative",
  borderRadius: "20px",
  padding: "0",
  marginRight: '40px',
  width: '47.6px',
  height: '47.6px',
  color: "#fff",
  outline: 'none',
  border: 'none',
  backgroundColor: 'black'
};

const Header = ({setAlarmPopup, setDmPopup}) => {
  let navigate = useNavigate();

  return (
    <>
      <div className="navbarContainer">
        <nav>
            <ul className="navLink_container">
              <li className="document-container">도슨트</li>
              <li className="socialing-container">소셜링</li>
            </ul>      
            <div className="logo-container">
              <img 
                src= {logoImg}
                alt="logo"
                onClick={() =>{navigate("/")}}
                />
          </div>
         <div className="buttonGroup-box">
          <ButtonGroup style = {{
          }}>
            <button
              onFocus={(e) => {
                setAlarmPopup(true);
              }}
              onBlur={(e) => {
                setAlarmPopup(false);
              }}

                variant= "#000"
              bg="#000"
              style={buttonStyle}
              value = {false}
            >
              <AiOutlineBell size="47"></AiOutlineBell>
            </button>
            <button
                onFocus={(e) => {
                  setDmPopup(true);
                }}
                onBlur={(e) => {
                  setDmPopup(false);
                }}
               variant= "#000"
              bg="#000"
              vale={false}
              style={buttonStyle}
            >
              <FiSend size="42"></FiSend>
            </button>
            <button
              onClick={() => {
                navigate('/myPage')
              }}
              variant= "#000"
              bg="#000"
              active
              style={{
                position: "relative",
                outline: "none",
                borderRadius: "20px",
                padding: "0",
                width: "45.8px",
                border: '0',
                height: '45.8px',
                color: "#fff",
                backgroundColor: 'black'
              }}
            >
              <IoPersonCircleOutline size="47"></IoPersonCircleOutline>
            </button>
          </ButtonGroup>
          </div>    
          </nav>
            {/* {
              showPopup ? (
                <div className="popup">
                <div className="popup_inner">
                  <h2>Success!</h2>
                  <button 
                    className="close" 
                    onClick={(e)=>{
                    console.log(e.target.value)
                    setShowPopup(e.target.value)
                    }}>
                    Close me
                  </button>
                </div>
              </div>
              ): null
            } */}
      </div>
    </>
  );
};

export default Header;
