import styled from 'styled-components';
import { ReactComponent as Profile } from '../../../assets/images/profile.svg';

const Icon = styled(Profile)`
  cursor: pointer;
  padding: ${props => props.padding};
  fill: white;
  transition: 0.3s;
  &:hover {
    width: 50px;
    height: 50px;
    fill: #f9cf00;
    transition: 0.3s;
  }
`;

export default Icon;
