import React, { useState } from 'react';
import Icon from './styles';

function Logo() {
  const [isOpen, setIsOpen] = useState(false);
  const iconClicked = () => {
    setIsOpen(!isOpen);
  };
  return <Icon onClick={iconClicked} />;
}

export default Logo;
