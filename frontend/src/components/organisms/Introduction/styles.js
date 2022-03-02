import styled from 'styled-components';

const Container = styled.div`
  display: flex;
  width: 100%;
  min-width: 1000px;
  height: 500px;
  background: #000000;
`;
const SideContainer = styled.div`
  display: flex;
  width: 25%;
`;

const SideBottomContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  height: 50px;
  width: 100%;
  margin: auto 0 30% 0;
`;

const Label = styled.label`
  position: relative;
  width: 70px;
  height: 35px;
`;

const Switch = styled.input`
  display: none;
`;

const Slider = styled.span`
  display: inline-block;
  width: 100%;
  height: 100%;
  position: relative;
  cursor: pointer;
  background-color: black;
  border-radius: 34px;
  transition: 0.4s;
  border: 1px solid #f9cf00;
  &::before {
    transform: ${props =>
      props.listType === 'discovery' ? 'translateX(35px)' : ''};
    position: absolute;
    content: '';
    height: 30px;
    width: 30px;
    left: 3px;
    bottom: 3px;
    background-color: #f9cf00;
    transition: 0.5s;
    border-radius: 50%;
  }
`;
{
  /* <label className="switch">
  <input id="switch" type="checkbox" />
  <span className="slider"></span>
</label>; */
}

export { Container, SideContainer, Label, Switch, Slider, SideBottomContainer };
