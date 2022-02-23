import React from 'react';
import {
  LogoContainer,
  HeaderLeftContainer,
  HeaderRightContainer,
} from './styles';
import TextButton from '../../atoms/TextButton/Button';
import { DM, Logo, Notice, Profile } from '../../atoms';

function Header() {
  const isLoggin = false;
  return (
    <>
      <HeaderLeftContainer>
        <TextButton>도슨트</TextButton>
        <TextButton>소셜링</TextButton>
      </HeaderLeftContainer>
      <LogoContainer>
        <Logo />
      </LogoContainer>
      <HeaderRightContainer>
        {isLoggin ? (
          <>
            <TextButton>로그인</TextButton>
            <TextButton>회원가입</TextButton>
          </>
        ) : (
          <>
            <Notice padding="20px" />
            <DM padding="20px" />
            <Profile padding="20px" />
          </>
        )}
      </HeaderRightContainer>
    </>
  );
}

export default Header;
