import styled from 'styled-components';
import { ReactComponent as xmark } from '../../../assets/images/xmark.svg';

const Modal = styled.div`
  position: fixed;
  visibility: ${props => (props?.isOpen ? 'visible' : 'hidden')};
  opacity: ${props => (props?.isOpen ? '1' : '0')};
  transition: 0.3s;
  top: 0;
  left: 0;
  width: 100%;
  min-width: 1000px;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1;
`;
const SignContainer = styled.div`
  position: fixed;
  display: flex;
  top: 120px;
  width: 60%;
  min-width: 600px;
  left: max(20%, 200px);
  height: 500px;
`;

const LeftContainer = styled.div`
  width: 50%;
  height: 100%;
  background: black;
  border-top-left-radius: 20px;
  border-bottom-left-radius: 20px;
  border: 1px solid gray;
`;

const RightContainer = styled.div`
  display: flex;
  width: 50%;
  height: 100%;
  background: white;
  border-top-right-radius: 20px;
  border-bottom-right-radius: 20px;
  border: 1px solid gray;
`;

const Xmark = styled(xmark)`
  fill: white;
  position: absolute;
  cursor: pointer;
  width: 30px;
  height: 30px;
  top: 100px;
  right: 15%;
`;

const textButtonStyle = {
  fontSize: '24px',
  fontWeight: 'bold',
  padding: '20px',
  color: 'white',
  Hover: {
    fontSize: '28px',
  },
};

export {
  Modal,
  SignContainer,
  LeftContainer,
  RightContainer,
  textButtonStyle,
  Xmark,
};
