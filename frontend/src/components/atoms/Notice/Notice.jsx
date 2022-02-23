import React from 'react';
import Icon from './styles';
import propTypes from 'prop-types';

function Notice({ padding = '' }) {
  return <Icon padding={padding} />;
}

Notice.propTypes = {
  padding: propTypes.string,
};

export default Notice;
