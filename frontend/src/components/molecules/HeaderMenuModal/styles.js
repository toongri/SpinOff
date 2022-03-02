import styled from 'styled-components';

const IconContainer = styled.div`
  position: relative;
`;

const ModalContainer = styled.div`
  display: ${props => (props?.isOpened ? '' : 'none')};
  position: absolute;
  width: 300px;
  height: 400px;
  left: -280px;
  bottom: -380px;
  background: #ffffff;
  box-shadow: 0px 4px 4px rgba(0, 0, 0, 0.25);
  border-radius: 30px;
  opacity: 0.9;
  z-index: 1;
`;

const UnreadMessage = styled.div`
  position: absolute;
  right: 0;
  top: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  width: 17px;
  height: 17px;
  border-radius: 50%;
  padding: 10px;
  font-size: 18px;
  background: #f24860;
`;

export { IconContainer, ModalContainer, UnreadMessage };
