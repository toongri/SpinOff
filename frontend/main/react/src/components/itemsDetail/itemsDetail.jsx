import React from 'react';
import styles from './itemsDetail.module.css'

const ItemsDetail = ({item, item: {snippet}}) => {
  return (
    <div>
      <img className = {styles.thumbnails} src={snippet.thumbnails.medium.url} alt="thumnails" />
      <img src="" alt="" />
    </div>
  );
};

export default ItemsDetail;