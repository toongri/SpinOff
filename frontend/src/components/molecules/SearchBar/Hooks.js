import { useState } from 'react';

const useFocus = initialValue => {
  const [focused, setFocused] = useState(initialValue);
  const onFocus = () => {
    setFocused(true);
  };
  return { focused, setFocused, onFocus };
};

const useInput = (initialValue, validator) => {
  const [value, setValue] = useState(initialValue);
  const onChange = e => {
    const {
      target: { value },
    } = e;
    let willUpdate = true;
    if (typeof validator === 'function') {
      willUpdate = validator(value);
    }
    if (willUpdate) {
      setValue(value);
    }
  };
  return { value, onChange };
};

export { useInput, useFocus };
