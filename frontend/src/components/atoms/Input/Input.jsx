import React from 'react';
import InputArea from './styles';
import propTypes from 'prop-types';

function Input({ style = {}, onChange }) {
  console.log(style);
  return <InputArea Style={style} onChange={e => onChange(e)} />;
}

Input.propTypes = {
  style: propTypes.object,
  onChange: propTypes.func,
};

export default Input;
