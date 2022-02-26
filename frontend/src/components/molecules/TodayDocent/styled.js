import styled from 'styled-components';

const Container = styled.div`
  position: relative;
  width: 50%;
  height: 100%;
`;

const ImageContainer = styled.div`
  position: relative;
  display: flex;
  width: 100%;
  height: 90%;
  margin-top: 30px;
  user-select: none;
`;
const Image = styled.div`
  position: absolute;
  display: table-cell;
  vertical-align: middle;
  width: ${props => (props.index === props.indicator ? '40%' : '25%')};
  height: ${props => (props.index === props.indicator ? '80%' : '50%')};
  top: ${props => (props.index === props.indicator ? '5%' : '20%')};
  left: ${props => (props.index === props.indicator ? '30%' : '')};
  left: ${props =>
    props.index * 1 === (props.indicator * 1 + 1) % 3 ? '75%' : ''};
  left: ${props =>
    props.index * 1 === (props.indicator * 1 + 2) % 3 ? '0px' : ''};
  opacity: ${props => (props.index === props.indicator ? '1' : '0.9')};
  transition: 0.3s;
`;

const Poster = styled.img`
  width: 100%;
  height: 100%;
  border-radius: 30px;
`;

const RecommendMessage = styled.div`
  position: absolute;
  width: 60%;
  top: 40%;
  left: 20%;
  font-weight: 800;
  font-size: 32px;
  text-align: center;
  user-select: none;
  color: #ffffff;
  text-shadow: 0px 4px 4px rgba(0, 0, 0, 0.25), 0px 4px 4px rgba(0, 0, 0, 0.25),
    0px 4px 50px #000000;
`;
export { Container, ImageContainer, Image, Poster, RecommendMessage };
