import React, {useRef, useEffect, useState} from 'react';
import './search.scss';
import Button from 'react-bootstrap/Button'
import {FiSearch} from'react-icons/fi';
import {useDispatch, useSelector} from 'react-redux';
import store from '../redux/store'
import axios from 'axios';
import {doSearch} from '../redux/actions';
import Masonry from '../masonry/masonry'
import {SEARCH} from '../redux/actions'

store.subscribe(() =>{
  console.log(store)
})

const Search = ({setPopup, popup}) => {
  const inputRef = useRef('');
  const [query, setQuery] = useState('');
  const [hasMore, setHasMore] = useState(false);
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);

  const dispatch = useDispatch();

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
    })
    .catch((e) =>{
      console.log('failed')
      dispatch({
        type: SEARCH,
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
       ]
      });
      if(axios.isCancel(e)) return
    })

    return () => cancel();

  }, [query])

 
  return (
    <>
    <div style = {
      {
        display: 'flex',
        justifyContent: 'center'
      }
    }>
    
    <div className='wrapper'>
    <div className="input-container">
     <div class = 'data-container'>
    <Button
     id="button-addon1"
     style={{
       border: 0,
       backgroundColor: '#f1f2f6',
       color: "black",
       borderRadius: "50px 0 0 50px",
       padding:'6px 10px 6px 15px'
     }
    }
     onClick = {handleSearch}
     > 
     <FiSearch 
     size = {39.4}
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
      onFocus = {() =>{
        setPopup(true)
         document.body.style.backgroundColor = 'rgba(0, 0, 0, 0.34)';
      }}
      onBlur = {() =>{
        setPopup(false)
        document.body.style.backgroundColor = '#fff';
      }
      }
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
  {
    popup&&(
    <div className='popup-container'>
      <div className='popup-info-container'>
        <div className='current-record'>
          <p>최근 검색 기록</p>
          <div className='curent-record-tags'>
            <span>어바웃 타임</span>
            <span>크리스마스</span>
            <span>해리포터</span>
            <span>판타지</span>
            <span>다크나이트</span>
            <span>마블</span>
          </div>
        </div>

        <div className='popular-hashtags-container'>
          <p>인기 해시태그</p>
          <ul className='popular-hashtags'>
            <li><span>#어바웃 타임</span></li>
            <li><span>#크리스마스</span></li>
            <li><span>#해리포터</span></li>
            <li><span>#겨울필수_ 영화 리스트</span></li>
            <li><span>#겨울필수_영화 리스트</span></li>
            <li><span>#여름에_보기좋은_뮤지컬영화</span></li>
            <li><span>#여름에_보기좋은_뮤지컬영화</span></li>
            <li><span>#여름에_보기좋은_뮤지컬영화</span></li>
          </ul>
        </div>
        {/*curation movies*/}

        <div className='curation-movies-container'>
          <p>큐레이션된 영화</p>
          <div className = "images-container">
            <div className='image-info'>
              <div className = "image-box">
                <img src = "https://movie-phinf.pstatic.net/20211215_297/1639556766975z0641_JPEG/movie_image.jpg"/>
              </div>
              <p className='movie-title'>스파이더맨: 노웨이홈</p>
            </div>
            <div className='image-info'>
              <div className = "image-box">
                <img src = "https://movie-phinf.pstatic.net/20210512_139/1620799657168vGIqq_JPEG/movie_image.jpg"/>
              </div>
              <p className='movie-title'>크루엘라</p>
            </div>
            <div  className='image-info'>
              <div className = "image-box">
                <img src = "https://movie-phinf.pstatic.net/20111222_256/1324527530062tFbqt_JPEG/movie_image.jpg"/>
              </div>
              <p className='movie-title'>나홀로 집에</p>
            </div >
            <div className='image-info'>
              <div className = "image-box">
                <img src = "https://movie-phinf.pstatic.net/20131115_243/1384498185621awKv1_JPEG/movie_image.jpg"/>
              </div>
              <p className='movie-title'>어바웃 타임</p>
            </div>
          </div>
        </div>
        </div>
    </div> 
  )}
  </div>
  </div>
  </div>
  </>
  );
};

export default Search;