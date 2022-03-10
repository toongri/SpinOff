import styled from 'styled-components';
import { ReactComponent as external } from '../../../assets/images/external.svg';
import { ReactComponent as more } from '../../../assets/images/more.svg';

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
const ImageContainer = styled.div`
  position: relative;
  cursor: pointer;
  &:hover div {
    visibility: visible;
    opacity: 1;
    transition: 0.4s;
  }
`;
const PostImage = styled.img`
  border-radius: 30px;
  border: 0;
  height: auto;
  min-height: 150px;
  max-width: 100%;
  vertical-align: middle;
`;
const ImageHover = styled.div`
  position: absolute;
  visibility: hidden;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  background: rgba(39, 35, 32, 0.6);
  border-radius: 30px;
  transition: 0.4s;
  opacity: 0;
`;

const External = styled(external)`
  position: absolute;
  bottom: 10px;
  right: 40px;
  background: white;
  border-radius: 20px;
  padding: 5px;
`;
const More = styled(more)`
  position: absolute;
  bottom: 10px;
  right: 10px;
  background: white;
  border-radius: 20px;
  padding: 5px;
`;

export {
  PostContainer,
  PostCaption,
  PostImage,
  ImageContainer,
  ImageHover,
  External,
  More,
};
