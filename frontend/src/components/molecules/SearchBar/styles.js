import styled from 'styled-components';
import { ReactComponent as Icon } from '../../../assets/images/magnifier.svg';

const Magnifier = styled(Icon)`
  padding: 10px;
  cursor: pointer;
`;

const SearchBarContainer = styled.div`
  position: absolute;
  display: flex;
  align-items: center;
  top: 115px;
  left: 50%;
  width: 700px;
  height: 70px;
  margin-left: -350px;
  border-radius: 50px;
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0px 4px 10px 3px rgba(0, 0, 0, 0.25);
`;

export { Magnifier, SearchBarContainer };
