import React, {useRef, useEffect, useState} from 'react';
import './search.scss';
import Button from 'react-bootstrap/Button'
import {FiSearch} from'react-icons/fi';
import {useDispatch, useSelector} from 'react-redux';
import axios from 'axios';
import Masonry from '../masonry/masonry'

const Search = () => {
  const inputRef = useRef('');
  const [query, setQuery] = useState('');
  const [hasMore, setHasMore] = useState(false);
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);

  const dispatch = useDispatch();
  const pinAllon = useSelector((state) => state.pinAllOn)
  const pinOn = useSelector(state => state.pinOn)

  const handleSearch = (e) => {
    const query = inputRef.current.value;
    console.log(query)
    setQuery(query);
    console.log(query)
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
      console.log('failed')
 
      dispatch({
         type: "SEARCH",
         query: query,
          items: [
            'https://movie-phinf.pstatic.net/20120329_250/1332987174058pwlne_JPEG/movie_image.jpg"',
           'https://movie-phinf.pstatic.net/20211215_297/1639556766975z0641_JPEG/movie_image.jpg',
           'https://movie-phinf.pstatic.net/20161014_50/147640824152266AVn_JPEG/movie_image.jpg',
           'https://movie-phinf.pstatic.net/20160427_273/1461725031863moaJw_JPEG/movie_image.jpg',
           'https://movie-phinf.pstatic.net/20161011_117/1476149660166LvI3l_JPEG/movie_image.jpg',
           'https://movie-phinf.pstatic.net/20210421_37/1618971733493B4ykS_JPEG/movie_image.jpg',
           'https://movie-phinf.pstatic.net/20211221_176/1640055647361DAXCF_JPEG/movie_image.jpg',
           'https://movie-phinf.pstatic.net/20211231_136/1640927037740RqKuo_JPEG/movie_image.jpg',
           'https://movie-phinf.pstatic.net/20220126_99/16431781439406dpnP_JPEG/movie_image.jpg',
           'https://movie-phinf.pstatic.net/20120426_172/1335428116411i030K_JPEG/movie_image.jpg',
           'https://movie-phinf.pstatic.net/20220125_283/1643101153330XwG2L_JPEG/movie_image.jpg',
           'https://movie-phinf.pstatic.net/20170928_85/1506564710105ua5fS_PNG/movie_image.jpg',
           'https://movie-phinf.pstatic.net/20211214_145/1639457181732CP8WJ_JPEG/movie_image.jpg',
           'https://movie-phinf.pstatic.net/20211221_186/1640076751690RmQ3w_JPEG/movie_image.jpg',
           'https://movie-phinf.pstatic.net/20211222_130/1640135864950wrGkx_JPEG/movie_image.jpg',
           'https://movie-phinf.pstatic.net/20211220_81/1639963885244tPBWI_JPEG/movie_image.jpg',
           'https://movie-phinf.pstatic.net/20150922_4/14429125602616zXzR_JPEG/movie_image.jpg',
           'https://movie-phinf.pstatic.net/20220126_227/1643182432127j8nuq_JPEG/movie_image.jpg',
           'https://movie-phinf.pstatic.net/20210915_104/1631681279096sdjNA_JPEG/movie_image.jpg'
           ],
           pinAllOn: true,
      })
      if(axios.isCancel(e)) return
    })

    return () => cancel();

  }, [query])

  useEffect(() =>{
    let cancel;
    axios.get(()=>{
      
    })
  }, [pinAllon])
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
    <div className = "container" style = {
      pinAllon ? {top: '90vh'} : {top: '65vh'}
      // pinOn ? {top: '135vh'} :{top: '65vh' }
      }
    >
      <Masonry />
    </div>
    </>
  );
};

export default Search;