import React from 'react';
import './itemsDetail.scss';
const ItemsDetail = ({item, key}) => {
 return (
   <div className = "item">
    <img 
    src = {item} />
   </div>
 )
}

export default ItemsDetail;