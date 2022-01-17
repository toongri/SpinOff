import React, {useRef, useEffect, useState} from 'react';
import './search.scss';

const Search = ({ onSearch, items }) => {
  const inputRef = useRef();
  const popupRef = useRef('null');
  const [value, setValue] = useState('');


  const handleSearch = () => {
    const value = inputRef.current.value;
    console.log(value);
    setValue(value);
    onSearch(value);
  }

  const activePopup = () =>{
    popupRef.current.style.display = 'flex';
  }

  const nonactivePopup = () =>{
    popupRef.current.style.display = 'none';
  }

  const onKeyPress = (e) => {
   if(e.key === 'Enter'){
    handleSearch();
   }
  }


  const handleSubmit = () =>{
    console.log(value);
    handleSearch();
  }

  return (
    <>
     <div className = 'search_container'>
    <div className = 'input_container'>
      <div className = 'input_buttonn' type = "submit">
        <i className="fas fa-search fa-1x"></i>
      </div>

      <div 
      className = 'submitContainer'
      >
        <input
          className = 'input' 
          type = "search" 
          placeholder = "Search"
          ref = {inputRef}
          onFocus = {activePopup}
          onBlur = {nonactivePopup}
          onKeyPress = {onKeyPress}
        /> 
       
       <select className='select_type'>
          <option defaultValue>all</option>
          <option value="1">큐레이터</option>
          <option value="2">테마</option>
        </select>
  </div>
  
        <div 
          className = 'popup'
          ref = {popupRef}
        >
        </div>
    </div>
    </div>

   
    </>
  );
};

export default Search;