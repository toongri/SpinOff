import React, {useRef, useEffect, useState} from 'react';
import './search.scss';
import Button from 'react-bootstrap/Button'
import {FiSearch} from'react-icons/fi';
import {useDispatch} from 'react-redux';
import axios from 'axios';

const Search = () => {
  const inputRef = useRef('');
  const [query, setQuery] = useState('');
  const [hasMore, setHasMore] = useState(false);
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);

  const dispatch = useDispatch();

  const handleSearch = (e) => {
    const query = inputRef.current.value;
    console.log(query)
    setQuery(query);
    inputRef.current.value = "";
  }

  useEffect(() =>{
    setData([])
  }, [query]);

  useEffect(() =>{
    let cancel;
   setLoading(true);

    axios.get(`url${query}`,{
      cancelToken: axios.CancelToken((c) => cancel = c)
    })
    .then((res) =>{
      setData(prevItems =>{
        return [...new Set([...prevItems, res.data])]
      })
      
      setHasMore(res.data > 0);
      setLoading(false);

         dispatch({
         type: "SEARCH",
         query: query,
         items: data,
         hasMore: hasMore,
         loading: loading
      })
    })
    .catch((e) =>{
      // dispatch({
      //    type: "SEARCH",
      //    query: query,
      //    items: ['Hello World', 'adfa'],
      // })
     
      if(axios.isCancel(e)) return
    })

    return () => cancel();

  }, [query])
  // const activePopup = () =>{
  //   // popupRef.current.style.display = 'flex';
  // }

  // const nonactivePopup = () =>{
  //   // popupRef.current.style.display = 'none';
  // }

  // const onKeyPress = (e) => {
  //  if(e.key === 'Enter'){
  //   handleSearch();
  //  }
  // }


  // const handleSubmit = () =>{
  //   console.log(value);
  //   handleSearch();
  // }

  return (
    <>
    <div style = {
      {
        display: 'flex',
        justifyContent: 'center'
      }
    }>
    
    <div
     className="input-container"
     >
     <div class = 'data-container'>
    <Button
     id="button-addon1"
     style={{
       border: 0,
       backgroundColor: '#f1f2f6',
       color: "black",
       borderRadius: "20px 0 0 20px",
       padding:'6px 10px 6px 13px'
     }
    }
     onClick = {handleSearch}
     > 
     <FiSearch 
     size = {22}
     style = {{
       color: '#f24860'
     }}
      onClick = {handleSearch}
     ></FiSearch>
    </Button>
    <div className = "input-select-container">
    <input
      ref = {inputRef}
      className='input'
      aria-label="Example text with button addon"
      aria-describedby="basic-addon1"
      onKeyPress = {(e) => {
        if(e.key === 'Enter'){
          handleSearch();
        }
        }}
    />
    <select className = "select">
      <option>ALL</option>
      <option>컬렉션</option>
      <option>큐레이터</option>
      <option>도슨트</option>
      <option>영화</option>
    </select>
    </div>
  </div>
  </div>
  </div>
    </>
  );
};

export default Search;