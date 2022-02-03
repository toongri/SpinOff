import React from 'react';
import {useEffect, useState} from 'react';
import axios from 'axios';

const useItemsSearch = (query, pageNumber) => {
  useEffect(() =>{
    axios.get(``, {
      
    })
  }, [query, pageNumber])
  return null
};

export default useItemsSearch;