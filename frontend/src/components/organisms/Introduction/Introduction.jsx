import React from 'react';
import TodayDocent from '../../molecules/TodayDocent';
import {
  Container,
  Label,
  Slider,
  Switch,
  SideContainer,
  SideBottomContainer,
} from './styles';
import { TextButton } from '../../atoms';

const buttonStyle = {
  color: 'white',
  padding: '0 20px',
  fontWeight: 'bold',
  fontSize: '20px',
};

function Introduction() {
  return (
    <Container>
      <SideContainer>
        <SideBottomContainer>
          <TextButton Style={buttonStyle}>팔로잉</TextButton>
          <Label>
            <Switch type="checkbox" />
            <Slider />
          </Label>
          <TextButton Style={buttonStyle}>발견</TextButton>
        </SideBottomContainer>
      </SideContainer>
      <TodayDocent />
      <SideContainer />
    </Container>
  );
}

export default Introduction;
