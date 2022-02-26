import React from 'react';
import Button from './styles';
import propTypes from 'prop-types';

function NormalButton({ onClick, type, Style, children }) {
  return (
    <Button Style={Style} onClick={onClick} type={type}>
      {children}
    </Button>
  );
}

NormalButton.propTypes = {
  onClick: propTypes.func,
  type: propTypes.string,
  Style: propTypes.object,
  children: propTypes.any,
};

export default NormalButton;
