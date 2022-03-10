import { useState } from 'react';

const useFocus = initialValue => {
  const [focused, setFocused] = useState(initialValue);
  const onFocus = () => {
    setFocused(true);
  };
  return { focused, setFocused, onFocus };
};

export default useFocus;
