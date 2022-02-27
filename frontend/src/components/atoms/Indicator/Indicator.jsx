import React, { useMemo } from 'react';
import { IndicatorContainer, IndicatorList } from './styles';
import propTypes from 'prop-types';

function Indicator({ onClick, hookValue, howMany }) {
  const dynamicIndicator = useMemo(() => {
    const array = [];
    for (let i = 0; i < howMany; i++) {
      array.push(
        <IndicatorList
          onClick={onClick}
          indicator={hookValue}
          index={i}
          data-index={i}
          key={i}
        ></IndicatorList>,
      );
    }
    return array;
  }, [hookValue]);

  return <IndicatorContainer>{dynamicIndicator}</IndicatorContainer>;
}

Indicator.propTypes = {
  onClick: propTypes.func,
  hookValue: propTypes.string,
  howMany: propTypes.number,
};

export default Indicator;
