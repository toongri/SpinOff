import React, { useState } from 'react';
import {
  LeftContainer,
  RightContainer,
  SignContainer,
  Modal,
  Xmark,
  textButtonStyle,
} from './styles';
import { TextButton } from '../../atoms';
import { SignForm } from '../../molecules';
function SignInUpModal() {
  const [open, setOpen] = useState(false);

  const onSignForm = () => {
    document.body.style.overflow = 'hidden';
    setOpen(prev => !prev);
  };

  const closeSignForm = () => {
    document.body.style.overflow = 'unset';
    setOpen(prev => !prev);
  };
  return (
    <>
      <TextButton onClick={onSignForm} Style={textButtonStyle}>
        로그인/회원가입
      </TextButton>
      <Modal isOpen={open}>
        <Xmark onClick={closeSignForm} />
        <SignContainer>
          <LeftContainer />
          <RightContainer>
            <SignForm />
          </RightContainer>
        </SignContainer>
      </Modal>
    </>
  );
}

export default SignInUpModal;
