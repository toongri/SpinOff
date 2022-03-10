import styled from 'styled-components';

const Button = styled.button`
  font-size: ${props => props?.Style?.fontSize || '16px'};
  font-weight: ${props => props?.Style?.fontWeight};
  padding: ${props => props?.Style?.padding};
  margin: ${props => props?.Style?.margin};
  text-decoration: ${props => props?.Style?.textDecoration};
  outline: none;
  color: ${props => props?.Style?.color};
  background: transparent;
  cursor: pointer;
  border: 0;
  transition: 0.3s;
  user-select: none;
  &:hover {
    font-size: ${props => props?.Style?.Hover?.fontSize};
    transition: 0.3s;
  }
  user-select: none;
`;

export default Button;
