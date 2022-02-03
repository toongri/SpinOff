import React, {useState, useEffect} from 'react';
import axios from 'axios'

const useItemSearch = () => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [items, setItems] = useState([]);
  const [hasMore, setHasMore] = useState(true);
  return (
    <div>
      
    </div>
  );
};

export default useItemSearch;