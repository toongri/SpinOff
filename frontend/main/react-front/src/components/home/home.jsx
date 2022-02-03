import React from 'react';
import Main from '../main/main';
import Header from '../header/header.jsx';
import {useEffect, useState} from 'react';
import Search from "../search/search";
import {createStore} from 'redux';
import {Provider, useSelector, useDispatch} from 'react-redux';
import axios from 'axios';

const reducer = (currentState, action) =>{
  if(currentState === undefined){
    return {
      query: '',
      items : [],
      pageNumber: 1,
      hasMore: false,
      loading: true
    }
  }

  const newState = {...currentState};

  if(action.type === 'SEARCH'){
    newState.items = action.items;
    newState.query = action.query;
    newState.loading = action.loading;
  }

  if(action.type === 'UPDATE' ){
    newState.pageNumber = action.pageNumber;
  }

  return newState;
}

const store = createStore(reducer);

const Home = () => {
    // const [items, setItems] = useState([]);
   useEffect(() => {
    
   }, [store])

    return (
        <div>
            <Header />
            <Provider store = {store}>
              <Search/>
              <Main/>
            </Provider>
        </div>
    );
};

export default Home;