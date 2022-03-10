import React from 'react';
import {
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
} from './styles';
import { Input, NormalButton, TextButton } from '../../atoms';
import { useInput } from '../../../Hooks';

function SignForm() {
  const idInput = useInput('');
  const pwInput = useInput('');
  return (
    <Container>
      <LogIn />
      <InputContainer>
        <Input Style={inputStyle} {...idInput} />
        <PlaceHolder>아이디</PlaceHolder>
      </InputContainer>
      <InputContainer>
        <Input Style={inputStyle} {...pwInput} />
        <PlaceHolder>비밀번호</PlaceHolder>
      </InputContainer>
      <NormalButton Style={normalButtonStyle}>로그인</NormalButton>
      <TextButton Style={textButtonStyle}>Spin-off 회원가입</TextButton>
      <TextButton Style={textButtonStyle2}>아이디/비밀번호 찾기</TextButton>
      <NaverContainer>
        <Naver />
        <TextButton Style={textButtonStyle3}>
          네이버로
          <br />
          시작하기
        </TextButton>
      </NaverContainer>
      <KakaoContainer>
        <Kakao />
        <TextButton Style={textButtonStyle3}>
          카카오톡으로
          <br />
          시작하기
        </TextButton>
      </KakaoContainer>
    </Container>
  );
}

export default SignForm;
