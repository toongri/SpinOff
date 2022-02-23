import React from 'react';
import { Magnifier, SearchBarContainer } from './styles';
import { Input } from '../../atoms';

function SearchBar() {
  const style = {
    backGround: 'rgba(255, 255, 255, 0.8)',
    padding: '13px 15px',
  };

  return (
    <SearchBarContainer>
      <Magnifier />
      <Input style={style} />
    </SearchBarContainer>
  );
}

export default SearchBar;
