import styled from 'styled-components';

const HeaderLeftContainer = styled.div`
  display: flex;
  align-items: center;
`;

const HeaderRightContainer = styled.div`
  width: 30%;
  display: felx;
  align-items: center;
  justify-content: flex-end;
  margin-left: auto;
`;

const LogoContainer = styled.div`
  position: absolute;
  left: 50%;
  margin-left: -127.5px;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background: #000000;
`;

export { LogoContainer, HeaderLeftContainer, HeaderRightContainer };
