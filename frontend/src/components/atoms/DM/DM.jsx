import React from 'react';
import Icon from './styles';
import propTypes from 'prop-types';

function DM({ padding = '' }) {
  return <Icon padding={padding} />;
}

DM.propTypes = {
  padding: propTypes.string,
};
export default DM;
