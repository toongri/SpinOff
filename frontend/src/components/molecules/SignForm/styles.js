import styled from 'styled-components';
import { ReactComponent as login } from '../../../assets/images/login.svg';
import { ReactComponent as naver } from '../../../assets/images/naver.svg';
import { ReactComponent as kakao } from '../../../assets/images/kakao.svg';

const Container = styled.div`
  margin-top: auto;
  display: flex;
  flex-wrap: wrap;
  width: 100%;
`;

const LogIn = styled(login)`
  margin: 20px auto;
`;

const InputContainer = styled.div`
  position: relative;
  border: 1px solid #f24860;
  width: 80%;
  height: 50px;
  margin: 5px auto;
  border-radius: 50px;
  box-sizing: border-box;
`;

const PlaceHolder = styled.span`
  position: absolute;
  top: 15px;
  left: 20px;
  font-weight: bold;
  color: gray;
  opacity: 0.8;
  transition: 0.3s;
  user-select: none;
`;

const Naver = styled(naver)`
  cursor: pointer;
`;
const Kakao = styled(kakao)`
  cursor: pointer;
`;
const NaverContainer = styled.div`
  display: flex;
  flex-wrap: wrap;
  width: 120px;
  justify-content: center;
  align-items: center;
  text-align: center;
  margin: 15px 25px 15px auto;
`;
const KakaoContainer = styled.div`
  display: flex;
  flex-wrap: wrap;
  width: 120px;
  justify-content: center;
  align-items: center;
  margin: 15px auto 15px 25px;
`;

const textButtonStyle = {
  color: '#A7A7A7',
  fontWeight: 'bold',
  textDecoration: 'underline',
  margin: '15px auto 15px 10%',
};
const textButtonStyle2 = {
  color: '#F9CF00',
  fontWeight: 'bold',
  textDecoration: 'underline',
  margin: '15px 10% 15px auto',
};
const textButtonStyle3 = {
  color: '#A7A7A7',
  padding: '10px',
};

const normalButtonStyle = {
  color: 'black',
  fontWeight: 'bold',
  fontSize: '20px',
  background: '#F9CF00',
  width: '80%',
  height: '50px',
  padding: '15px 0',
  borderRadius: '50px',
  margin: '5px auto',
};
const inputStyle = {
  padding: '20px 20px 10px 20px',
  backGround: 'transparent',
  width: '100%',
  fontSize: '16px',
  focused: {
    transform: 'translate(0,-10px)',
    fontSize: '14px',
  },
};
export {
  Container,
  LogIn,
  InputContainer,
  PlaceHolder,
  Naver,
  Kakao,
  NaverContainer,
  KakaoContainer,
  inputStyle,
  textButtonStyle,
  textButtonStyle2,
  textButtonStyle3,
  normalButtonStyle,
};
