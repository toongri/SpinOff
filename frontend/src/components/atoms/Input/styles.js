import styled from 'styled-components';

const InputArea = styled.input`
  outline: 0;
  border: 0;
  resize: none;
  padding: ${props => props.Style.padding};
  font-size: 20px;
  width: 100%;
  box-sizing: border-box;
  background: transparent;
`;

export default InputArea;
