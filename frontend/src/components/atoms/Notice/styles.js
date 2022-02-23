import styled from 'styled-components';
import { ReactComponent as Notice } from '../../../assets/images/notice.svg';

const Icon = styled(Notice)`
  padding: ${props => props.padding};
  cursor: pointer;
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
