import React from 'react';
import { Header, Introduction } from '../components/organisms';
import styled from 'styled-components';

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
    </>
  );
}

export default App;
