import React from 'react';
import InputArea from './styles';
import propTypes from 'prop-types';

function Input({ Style = {}, value, onChange, onFocus }) {
  //console.log(style);
  return (
    <InputArea
      Style={Style}
      value={value}
      onChange={onChange}
      onFocus={onFocus}
    />
  );
}

Input.propTypes = {
  Style: propTypes.object,
  value: propTypes.string,
  onChange: propTypes.func,
  onFocus: propTypes.func,
};

export default Input;
