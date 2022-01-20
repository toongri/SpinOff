import React, {useEffect} from 'react';
import ItemsDetail from '../itemsDetail/itemsDetail';
import { Button } from 'react-bootstrap';
import './main.scss';
import { makeStyles } from '@mui/styles';
import {BsPencilFill} from 'react-icons/bs';
import { useNavigate } from 'react-router-dom';
import Masonry from 'react-masonry-css';

const useStyle = makeStyles({
  btn:{
    color: 'black'
  }
})

const breakpointColumnsObj = {
  default: 5,
  1500: 4,
  1100: 3,
  700: 2,
  500: 1,
};


const Main = ({ onSearch, items }) => {
  const classes = useStyle();
  let navigate = useNavigate();

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
        <div>
          <Masonry
           breakpointCols={breakpointColumnsObj}
           className="my-masonry-grid"
           columnClassName="my-masonry-grid_column"
          >
           
              {sample.map((item, index) => {
                  return  <ItemsDetail key = {index} item = {item}/>
              })}
          
          </Masonry>
          </div>  
        <div className='buttonBox'>
          <Button 
            onClick={() => {
            navigate("/pin-build")
            }}
            variant = "secondary"
            style = {{
              color: "#000",
              backgroundColor: "#fff",
              borderRadius: "50%",
              height: "50px",
              border: "none",
              boxShadow:" 0px 0px 18px -10px #404040"
            }}
          ><BsPencilFill size = "30"></BsPencilFill></Button>
        </div>
        </div>   
    </>
  );
};

export default Main;