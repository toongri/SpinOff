import styled from 'styled-components';

const InputArea = styled.input`
  display: block;
  outline: 0;
  border: 0;
  resize: none;
  padding: ${props => props?.Style?.padding};
  margin: ${props => props?.Style?.margin};
  font-size: ${props => props?.Style?.fontSize || '20px'};
  box-sizing: border-box;
  background: ${props => props?.Style?.backGround};
  border-radius: ${props => props?.Style?.borderRadius};
  border: ${props => props?.Style?.border};
  width: ${props => props?.Style?.width};
  &:focus + span {
    transform: ${props => props?.Style?.focused?.transform};
    font-size: ${props => props?.Style?.focused?.fontSize};
    transition: 0.3s;
  }
`;

export default InputArea;
