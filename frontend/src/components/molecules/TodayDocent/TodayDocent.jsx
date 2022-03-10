import React, { useState } from 'react';
import NormalButton from '../../atoms/NormalButton/NormalButton';
import {
  Container,
  ImageContainer,
  Image,
  Poster,
  RecommendMessage,
} from './styled';
import { Indicator } from '../../atoms';

const buttonStyle = {
  background: '#F24860',
  borderRadius: '20px',
  color: 'white',
  display: 'block',
  margin: '20px auto',
  padding: '10px 20px',
  fontWeight: 'bold',
};

function TodayDocent() {
  const [indicator, setIndicator] = useState('0');
  const indicatorClicked = e => {
    setIndicator(e.target.dataset.index);
  };
  return (
    <Container>
      <ImageContainer>
        <Image index="0" indicator={indicator}>
          <Poster
            src="https://cdn.pixabay.com/photo/2021/10/16/05/43/love-6713977_960_720.jpg"
            alt=""
          />
        </Image>
        <Image index="1" indicator={indicator}>
          <Poster
            src="https://cdn.pixabay.com/photo/2018/09/30/10/21/woman-3713108_960_720.jpg"
            alt=""
          />
        </Image>
        <Image index="2" indicator={indicator}>
          <Poster
            src="https://cdn.pixabay.com/photo/2017/09/14/11/47/sky-2748735_960_720.jpg"
            alt=""
          />
        </Image>
        <RecommendMessage>
          추천하는 도슨트 컨텐츠 <br />
          지금 읽어 보세요
          <NormalButton Style={buttonStyle}>
            오늘의 도슨트 보러가기
          </NormalButton>
          <Indicator
            onClick={indicatorClicked}
            hookValue={indicator}
            howMany={3}
          />
        </RecommendMessage>
      </ImageContainer>
    </Container>
  );
}

export default TodayDocent;
