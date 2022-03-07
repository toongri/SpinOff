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
import { useSelector, useDispatch } from 'react-redux';
import {
  toggleFilter,
  discovery,
  following,
} from '../../../store/ListFilter/action';

const buttonStyle = {
  color: 'white',
  padding: '0 15px',
  fontWeight: 'bold',
  fontSize: '20px',
};

function Introduction() {
  const listFilter = useSelector(state => state.listFilterReducer);
  const dispatch = useDispatch();

  const Toggle = () => {
    dispatch(toggleFilter());
  };
  const Discovery = () => {
    dispatch(discovery());
  };
  const Following = () => {
    dispatch(following());
  };

  return (
    <Container>
      <SideContainer>
        <SideBottomContainer>
          <TextButton onClick={Following} Style={buttonStyle}>
            팔로잉
          </TextButton>
          <Label>
            <Switch type="checkbox" />
            <Slider onClick={Toggle} listType={listFilter} />
          </Label>
          <TextButton onClick={Discovery} Style={buttonStyle}>
            발견
          </TextButton>
        </SideBottomContainer>
      </SideContainer>
      <TodayDocent />
      <SideContainer />
    </Container>
  );
}

export default Introduction;
