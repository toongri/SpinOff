import React, {useState} from "react";
import "./header.scss";
import Button from "react-bootstrap/Button";
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
  marginRight: '20px',
  width: "45.8px",
  height: '45.8px',
  color: "#fff",
  outline: 'none',
  border: 'none',
  backgroundColor: 'black'
};

const Header = () => {

  let navigate = useNavigate();
  const [showPopup, setShowPopup] = useState(false)

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
              onClick={(e) => {
                console.log(e.target.value)
                setShowPopup(e.target.value)
              }}
                variant= "#000"
              bg="#000"
              style={buttonStyle}
              value = {false}
            >
              <AiOutlineBell size="26"></AiOutlineBell>
            </button>
            <button
              onClick={() => {
      
              }}
               variant= "#000"
              bg="#000"
              active
              style={buttonStyle}
            >
              <FiSend size="26"></FiSend>
            </button>
            <button
              onClick={() => {}}
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
              <IoPersonCircleOutline size="26"></IoPersonCircleOutline>
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
