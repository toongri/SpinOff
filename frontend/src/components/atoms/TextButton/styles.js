import styled from 'styled-components';

const Button = styled.button`
  font-size: 24px;
  padding: 20px;
  outline: none;
  color: white;
  background: transparent;
  cursor: pointer;
  border: 0;
  transition: 0.3s;
  user-select: none;
  &:hover {
    font-size: 28px;
    transition: 0.3s;
  }
`;

export default Button;
