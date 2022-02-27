import styled from 'styled-components';

const Button = styled.button`
  outline: none;
  border: 0;
  cursor: pointer;
  border-radius: ${props => props?.Style?.borderRadius};
  width: ${props => props?.Style?.width};
  background: ${props => props?.Style?.background};
  color: ${props => props?.Style?.color};
  display: ${props => props?.Style?.display};
  margin: ${props => props?.Style?.margin};
  padding: ${props => props?.Style?.padding};
  font-size: ${props => props?.Style?.fontSize};
  font-weight: ${props => props?.Style?.fontWeight}; ;
`;

export default Button;
