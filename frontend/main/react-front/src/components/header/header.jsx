import React from "react";
import "./header.scss";
import Navbar from "react-bootstrap/Navbar";
import Nav from "react-bootstrap/Nav";
import Button from "react-bootstrap/Button";
import Form from 'react-bootstrap/Form'
import {IoPersonCircleOutline} from'react-icons/io5'

const Header = () => {
  return (
    <>
      <div className="navbarContainer">
        <Navbar
          bg="dark"
          variant="light"
          expand="lg"
          style={{
            width: "100%",
          }}
        >
          <Nav
            className="me-auto"
            style={{
              height: "120px",
            }}
          >
            <div className="navLink_container">
              <Nav.Link
                href="#home"
                style={{
                  color: "white",
                  marginRight: "20px",
                  marginTop: "60px",
                }}
              >
                도슨트
              </Nav.Link>
              <Nav.Link
                href="#features"
                style={{
                  color: "white",
                  marginTop: "60px",
                }}
              >
                소셜링
              </Nav.Link>
            </div>
          </Nav>
          <Button
            onClick={() =>{
            
            }}
            variant="dark"
            bg=""
            active
            style={{
              position: "relative",
              right: "3%",
              outline: "none",
              borderRadius: "20px",
              padding: '0'
            }}
          >
            <IoPersonCircleOutline size="35"></IoPersonCircleOutline>
          </Button>
        </Navbar>
      </div>
    </>
  );
};

export default Header;
