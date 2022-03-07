import React from 'react';
import Icon from './styles';
import propTypes from 'prop-types';

function Notice({ onClick }) {
  return <Icon onClick={onClick} />;
}

Notice.propTypes = {
  onClick: propTypes.func,
};

export default Notice;
