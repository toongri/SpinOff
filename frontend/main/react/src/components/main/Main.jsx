import React, {useEffect} from 'react';
import styles from './main.module.css';
import Search from '../search/search.jsx';
import ItemsDetail from '../itemsDetail/itemsDetail';
import Masonry from 'react-masonry-css';

const Main = ({ onSearch, items }) => {
  const breakpointColumnsObj = {
    default: 4,
    1100: 3,
    700: 2,
    500: 1
  };

  return (
    <>
        <div className = {styles.container}>           
          <Search onSearch = {onSearch} items = {items}/>
          <div className = {styles.masonryContainer}>
          <Masonry breakpointCols={breakpointColumnsObj}
            className={styles.my_masonry_grid}
            columnClassName={styles.my_masonry_grid_column}>
          {
            items.map(item => {
              console.log(item)
            return <ItemsDetail key = {item.snippet.publishTime} item = {item}/>
            })
          }
        </Masonry>
        </div>
        </div>   
    </>
  );
};

export default Main;