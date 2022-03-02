import React from 'react';
import Icon from './styles';
import propTypes from 'prop-types';

function DM({ onClick }) {
  return <Icon onClick={onClick} />;
}

DM.propTypes = {
  onClick: propTypes.func,
};
export default DM;
