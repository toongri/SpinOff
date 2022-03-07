import React from 'react';
import InputArea from './styles';
import propTypes from 'prop-types';

function Input({ style = {}, value, onChange, onFocus }) {
  //console.log(style);
  return (
    <InputArea
      Style={style}
      value={value}
      onChange={onChange}
      onFocus={onFocus}
    />
  );
}

Input.propTypes = {
  style: propTypes.object,
  value: propTypes.string,
  onChange: propTypes.func,
  onFocus: propTypes.func,
};

export default Input;
