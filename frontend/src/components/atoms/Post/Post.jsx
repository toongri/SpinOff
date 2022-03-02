import React from 'react';
import propTypes from 'prop-types';
import { PostContainer, PostCaption, PostImage } from './styles';

function Post({ title, poster }) {
  return (
    <PostContainer>
      <PostImage src={poster} alt={title} />
      <PostCaption>{title}</PostCaption>
    </PostContainer>
  );
}

Post.propTypes = {
  title: propTypes.string,
  poster: propTypes.string,
};

export default Post;
