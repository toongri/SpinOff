import React from 'react';
import { Container } from './styles';
import { SearchBar } from '../../molecules';
import { HeaderBar } from '..';

function Header() {
  return (
    <Container>
      <HeaderBar />
      <SearchBar />
    </Container>
  );
}

export default Header;
