import './App.css';
import Main from './components/main/main.jsx';
import Header from './components/header/header.jsx';
import {useEffect, useState} from 'react';

function App({network}) {
  const [items, setItems] = useState([]);
  /*
  단점
  1. key값이 나타난다는 것
  2. 컴포넌트에 네트워크 통신하는 로직이 들어 있다는 것
  -> 필요한 것을 컴포넌트에 주입 = Dependency injecntion
  */

  const search = query =>{
    network
    .search(query)
    .then(items => setItems(items))
  }

   useEffect(() => {
    
   }, [])

  return (
    <div>
      <Header />
      <Main  onSearch = {search} items = {items}/>
    </div>
  );
}

export default App;
