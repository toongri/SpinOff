import React from 'react';
import {
  LogoContainer,
  HeaderLeftContainer,
  HeaderRightContainer,
} from './styles';
import { Notice, DM, Logo, Profile, TextButton } from '../../atoms';
import HeaderMenuModal from '../HeaderMenuModal/HeaderMenuModal';

const buttonStyle = {
  fontSize: '24px',
  fontWeight: 'bold',
  padding: '20px',
  color: 'white',
  Hover: {
    fontSize: '28px',
  },
};

function Header() {
  const isLoggin = false;
  return (
    <>
      <HeaderLeftContainer>
        <TextButton Style={buttonStyle}>도슨트</TextButton>
        <TextButton Style={buttonStyle}>소셜링</TextButton>
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
            <HeaderMenuModal IconType={Notice} />
            <HeaderMenuModal IconType={DM} />
            <Profile padding="20px" />
          </>
        )}
      </HeaderRightContainer>
    </>
  );
}

export default Header;
