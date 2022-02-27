import React from 'react';
import propTypes from 'prop-types';
import Button from './styles';

function TextButton({ onClick, type, Style, children }) {
  return (
    <Button Style={Style} onClick={onClick} type={type}>
      {children}
    </Button>
  );
}

TextButton.propTypes = {
  onClick: propTypes.func,
  type: propTypes.string,
  Style: propTypes.object,
  children: propTypes.any,
};

export default TextButton;
