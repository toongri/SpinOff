import React from 'react';
import { Header, Introduction } from '../components/organisms';
import styled from 'styled-components';
import Masonry from '../components/organisms/Masonry/Masonry';

const AA = styled.div`
  margin-top: 150px;
  height: 100%;
`;

function App() {
  return (
    <>
      <Header />
      <AA>
        <Introduction />
      </AA>
      <Masonry />
    </>
  );
}

export default App;
