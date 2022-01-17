import React, {useEffect} from 'react';
import Search from '../search/search.jsx';
import ItemsDetail from '../itemsDetail/itemsDetail';
import Button from '@mui/material/Button';
import AddIcon from '@mui/icons-material/Add'
import Box from '@mui/material/Box';
import './main.scss';
import { makeStyles } from '@mui/styles';

const useStyle = makeStyles({
  btn:{
    color: 'black'
  }
})


const Main = ({ onSearch, items }) => {
  const classes = useStyle();

  const sample = [ 'https://cdn.pixabay.com/photo/2020/09/02/20/52/dock-5539524__340.jpg', 
  'https://cdn.pixabay.com/photo/2021/02/03/13/54/cupcake-5978060__340.jpg', 
  'https://cdn.pixabay.com/photo/2020/05/25/20/14/holland-iris-5220407__340.jpg', 
  'https://cdn.pixabay.com/photo/2020/10/08/17/39/waves-5638587__340.jpg', 
  'https://cdn.pixabay.com/photo/2019/01/30/11/17/zebra-3964360__340.jpg', 
  'https://cdn.pixabay.com/photo/2021/02/01/13/37/cars-5970663__340.png', 
  'https://cdn.pixabay.com/photo/2019/06/05/10/34/mimosa-4253396__340.jpg', 
  'https://cdn.pixabay.com/photo/2020/08/04/14/42/sky-5463015__340.jpg', 
  'https://cdn.pixabay.com/photo/2021/02/03/13/54/cupcake-5978060__340.jpg', 
  'https://cdn.pixabay.com/photo/2020/01/09/01/00/the-eye-on-the-greek-4751572__340.png', 
  'https://cdn.pixabay.com/photo/2021/01/30/12/19/couple-5963678__340.png', 
  'https://cdn.pixabay.com/photo/2021/01/23/07/53/dogs-5941898__340.jpg', 
  'https://cdn.pixabay.com/photo/2020/06/15/01/06/sunset-5299957__340.jpg', ];


  return (
    <>
        <div className = 'container'>           
          <Search onSearch = {onSearch} items = {items}/>
          <div className = "masonry_grid">
          {sample.map((item, index) => {
            return  <ItemsDetail key = {index} item = {item}/>
          })
           
          }
        </div>
        <Box
        component = "span"
          sx = {{
            p:2,
           color: 'text.secondary',
           position: 'relative',
          }}
        >
          <Button 
            variant="text"
            size = "large"
            startIcon = {<AddIcon/>}
          />
        </Box>
        </div>   
    </>
  );
};

export default Main;