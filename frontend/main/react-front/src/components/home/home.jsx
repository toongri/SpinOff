import React from 'react';
import Main from '../main/main';
import Header from '../header/header.jsx';
import {useEffect, useState} from 'react';
import Search from "../search/search";

const Home = ({network}) => {
    const [items, setItems] = useState([]);

  const search = query =>{
    network
    .search(query)
    .then(items => setItems(items))
  }

   useEffect(() => {
    
   }, [items])

    return (
        <div>
            <Header />
            <Search />
            <Main  onSearch = {search} items = {items}/>
        </div>
    );
};

export default Home;