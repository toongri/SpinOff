import styled from 'styled-components';
import { ReactComponent as Icon } from '../../../assets/images/magnifier.svg';

const Magnifier = styled(Icon)`
  padding: 10px;
  cursor: pointer;
`;

const SearchBarContainer = styled.div`
  position: fixed;
  display: flex;
  align-items: center;
  top: 120px;
  left: max(50%, 500px);
  width: 50%;
  min-width: 500px;
  height: 60px;
  margin-left: min(-25%, -250px);
  border-radius: 50px;
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0px 4px 10px 3px rgba(0, 0, 0, 0.25);
`;

export { Magnifier, SearchBarContainer };
