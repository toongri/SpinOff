import React, {useRef, useEffect, useState} from 'react';
import styles from './search.module.css';

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
     <div className ={styles.search_container}>
    <div className = {styles.input_container}>
      <div className = {styles.input_button} type = "submit">
        <i className="fas fa-search fa-1x"></i>
      </div>
      <div 
      className = {styles.submitContainer} 
      >
        <input
          className = {styles.input} 
          type = "search" 
          placeholder = "Search"
          ref = {inputRef}
          onFocus = {activePopup}
          onBlur = {nonactivePopup}
          onKeyPress = {onKeyPress}
        /> 
       
        <div className={styles.select}>
          <select>
              <option value="1">All</option>
              <option value="2">큐레이터</option>
              <option value="3">테마</option>
          </select>
        </div>
  </div>
        <div 
          className = {styles.popup}
          ref = {popupRef}
        >
        </div>
    </div>
    </div>

   
    </>
  );
};

export default Search;