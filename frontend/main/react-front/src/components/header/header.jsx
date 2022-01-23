import React from "react";
import "./header.scss";
import Navbar from "react-bootstrap/Navbar";
import Nav from "react-bootstrap/Nav";
import Button from "react-bootstrap/Button";
import { FiSend } from "react-icons/fi";
import { IoPersonCircleOutline } from "react-icons/io5";
import { AiOutlineBell } from "react-icons/ai";
import {ButtonGroup} from "react-bootstrap";

const buttonStyle = {
  position: "relative",
  right: "3%",
  outline: "none",
  borderRadius: "20px",
  padding: "0",
  marginRight: '10px'
};

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
          <ButtonGroup style = {{
            marginRight: '20px'
          }}>
            <Button
              onClick={() => {}}
              variant="dark"
              bg=""
              active
              style={buttonStyle}
            >
              <AiOutlineBell size="30"></AiOutlineBell>
            </Button>
            <Button
              onClick={() => {}}
              variant="dark"
              bg=""
              active
              style={buttonStyle}
            >
              <FiSend size="30"></FiSend>
            </Button>
            <Button
              onClick={() => {}}
              variant="dark"
              bg=""
              active
              style={buttonStyle}
            >
              <IoPersonCircleOutline size="30"></IoPersonCircleOutline>
            </Button>
          </ButtonGroup>
        </Navbar>
      </div>
    </>
  );
};

export default Header;
