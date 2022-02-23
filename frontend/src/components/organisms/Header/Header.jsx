import React from 'react';
import Container from './styles';
import { HeaderBar, SearchBar } from '../../molecules';

function Header() {
  return (
    <Container>
      <HeaderBar />
      <SearchBar />
    </Container>
  );
}

export default Header;
