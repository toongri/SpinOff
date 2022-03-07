import styled from 'styled-components';

const PostContainer = styled.figure`
  display: inline-block;
  background: transparent;
  //border: 1px solid rgba(0, 0, 0, 0.2);
  width: 200px;
  margin: 0;
  margin-bottom: 20px;
  padding: 10px;
  border-radius: 30px;
  box-shadow: rgba(50, 50, 93, 0.25);
`;

const PostCaption = styled.figcaption`
  padding: 10px;
`;

const PostImage = styled.img`
  border-radius: 30px;
  border: 0;
  height: auto;
  min-height: 150px;
  max-width: 100%;
  vertical-align: middle;
`;

export { PostContainer, PostCaption, PostImage };
