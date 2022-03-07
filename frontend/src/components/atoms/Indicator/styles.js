import styled from 'styled-components';

const IndicatorContainer = styled.ul`
  position: absolute;
  width: 50%;
  left: 25%;
  text-align: center;
  list-style: none;
  margin: 0;
  padding: 0;
`;
const IndicatorList = styled.li`
  display: inline-block;
  border-radius: 50%;
  background: ${props =>
    props.index === props.indicator * 1 ? '#fff' : '#2800EE'};
  position: relative;
  top: 0;
  cursor: pointer;
  margin: 0 4px;
  width: 15px;
  height: 15px;
`;

export { IndicatorContainer, IndicatorList };
