import React, {useRef, useEffect, useState} from 'react';
import './search.scss';
import Button from 'react-bootstrap/Button'
import InputGroup from 'react-bootstrap/InputGroup'
import FormControl from 'react-bootstrap/FormControl'
import {FiSearch} from'react-icons/fi';

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
    <div style = {
      {
        display: 'flex',
        justifyContent: 'center'
      }
    }>
    <InputGroup
     className="mb-3"
     style={{
       border: '0',
      borderRadius:'20px',
      position:'fixed',
      top: '180px',
      width: "55%",
      height: "45px",
      zIndex: '1',
      
    }}
     >
    <Button
     id="button-addon1"
     style={{
       border: 0,
       backgroundColor: '#f1f2f6',
       color: "black",
       borderRadius: "20px 0 0 20px",
       paddingLeft:'30px'
     }
    }
     > 
     <FiSearch></FiSearch>
    </Button>
    <FormControl
        className='input'
        style={{
          border: '0', 
          backgroundColor: '#f1f2f6',
          borderRadius:'0 20px 20px 0'
        }}
      aria-label="Example text with button addon"
      aria-describedby="basic-addon1"
    />
  </InputGroup>
  </div>
    </>
  );
};

export default Search;