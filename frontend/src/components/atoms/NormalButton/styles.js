import styled from 'styled-components';

const Button = styled.button`
  outline: none;
  border: 0;
  cursor: pointer;
  user-select: none;
  position: ${props => props?.Style?.position};
  border-radius: ${props => props?.Style?.borderRadius};
  width: ${props => props?.Style?.width};
  height: ${props => props?.Style?.height};
  background: ${props => props?.Style?.background};
  color: ${props => props?.Style?.color};
  display: ${props => props?.Style?.display};
  margin: ${props => props?.Style?.margin};
  padding: ${props => props?.Style?.padding};
  font-size: ${props => props?.Style?.fontSize};
  font-weight: ${props => props?.Style?.fontWeight};
  left: ${props => props?.Style?.left};
  top: ${props => props?.Style?.top};
  right: ${props => props?.Style?.right};
  bottom: ${props => props?.Style?.bottom};
  opacity: ${props => props?.Style?.opacity};
`;

export default Button;
