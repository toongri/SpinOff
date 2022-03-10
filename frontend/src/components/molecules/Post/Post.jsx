import React from 'react';
import propTypes from 'prop-types';
import {
  PostContainer,
  PostCaption,
  PostImage,
  ImageContainer,
  ImageHover,
  External,
  More,
} from './styles';
import { NormalButton } from '../../atoms';

const buttonStyle = {
  position: 'absolute',
  right: '10px',
  top: '10px',
  background: 'white',
  color: '#F24860',
  fontWeight: 'bold',
  borderRadius: '20px',
  padding: '10px 15px',
};

function Post({ title, poster }) {
  return (
    <PostContainer>
      <ImageContainer>
        <PostImage src={poster} alt={title} />
        <ImageHover>
          <NormalButton Style={buttonStyle}>저장</NormalButton>
          <External />
          <More />
        </ImageHover>
      </ImageContainer>
      <PostCaption>{title}</PostCaption>
    </PostContainer>
  );
}

Post.propTypes = {
  title: propTypes.string,
  poster: propTypes.string,
};

export default Post;
