import React, { useState, useEffect, useRef } from 'react';
import { IconContainer, ModalContainer, UnreadMessage } from './styles';
import propTypes from 'prop-types';

function HeaderMenuModal({ IconType }) {
  const [isOpen, setIsOpen] = useState(false);
  const modalEl = useRef();
  const handleClickedOutside = ({ target }) => {
    setIsOpen(prev => {
      if (prev && !modalEl.current.contains(target)) return false;
      return prev;
    });
  };
  useEffect(() => {
    window.addEventListener('mousedown', handleClickedOutside);
    return () => {
      window.removeEventListener('mousedown', handleClickedOutside);
    };
  }, []);
  const iconClicked = () => {
    setIsOpen(prev => !prev);
  };

  return (
    <IconContainer ref={modalEl}>
      <UnreadMessage>99+</UnreadMessage>
      <IconType onClick={iconClicked} />
      <ModalContainer isOpened={isOpen} />
    </IconContainer>
  );
}

HeaderMenuModal.propTypes = {
  IconType: propTypes.func,
};
export default HeaderMenuModal;
