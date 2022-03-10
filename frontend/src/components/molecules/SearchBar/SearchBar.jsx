import React, { useEffect, useRef } from 'react';
import { Magnifier, SearchBarContainer, SearchBarModal } from './styles';
import { Input } from '../../atoms';
import { useInput, useFocus } from '../../../Hooks';

function SearchBar() {
  const inputStyle = {
    width: '100%',
    padding: '13px 15px',
    backGround: 'transparent',
  };
  const searchHook = useInput('');
  const { focused, setFocused, onFocus } = useFocus(false);
  const inputEl = useRef();
  const handleClickedOutside = ({ target }) => {
    setFocused(prev => {
      if (prev && !inputEl.current.contains(target)) return false;
      return prev;
    });
  };

  useEffect(() => {
    window.addEventListener('mousedown', handleClickedOutside);
    return () => {
      window.removeEventListener('mousedown', handleClickedOutside);
    };
  }, []);

  return (
    <div ref={inputEl}>
      <SearchBarModal focused={focused}>asd</SearchBarModal>
      <SearchBarContainer>
        <Magnifier />
        <Input Style={inputStyle} {...searchHook} onFocus={onFocus} />
      </SearchBarContainer>
    </div>
  );
}

export default SearchBar;
