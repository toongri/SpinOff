import React from 'react';
import propTypes from 'prop-types';
import Button from './styles';
import stylePropType from 'react-style-proptype';

function TextButton({ onClick, type, style, children }) {
  return (
    <Button style={style} onClick={onClick} type={type}>
      {children}
    </Button>
  );
}

TextButton.propTypes = {
  onClick: propTypes.func,
  type: propTypes.string,
  style: stylePropType,
  children: propTypes.any,
};

export default TextButton;
