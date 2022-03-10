import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Container } from './styles';
import { Post } from '../../molecules';

function Masonry() {
  const dummy = [
    'https://w.namu.la/s/f9ed8fdf9e778b215ed0e05a8ba3ca2ae166dac4ac4b26d27d220d0fdd456511c6d5d5ce6473ab9b718ec1aa2594357e20b0dd8f29e5727270e82dc6b9aafaa472b0e17678ff3e5379145993552df58d84a3a60947036c28a4a5e0fdeef894f4',
    'https://img9.yna.co.kr/etc/inner/KR/2021/10/10/AKR20211010018200005_02_i_P4.jpg',
    'https://w.namu.la/s/5364250d393da1e171a89d596aa2c24e910276cab918bd235fdc05030265804c5667446c73cdbca74b650c86a7843e343e643fcc46ad19a80c64de54b5bcd510164ae35cfe977ca28a0a1a3da437b0f582875e0a2f4b3494c0ed39eb9b9f5610',
    'https://upload.wikimedia.org/wikipedia/commons/thumb/4/46/210928_Jisoo.jpg/250px-210928_Jisoo.jpg',
    'https://img.hankyung.com/photo/202112/BF.28372895.1.jpg',
    'https://cdn.topstarnews.net/news/photo/202104/869240_604006_560.jpg',
    'https://thumb.mtstarnews.com/06/2021/06/2021062909273777091_1.jpg/dims/optimize',
    'https://img.sbs.co.kr/newsnet/etv/upload/2021/07/08/30000699784.jpg',
    'https://img.etoday.co.kr/pto_db/2020/08/600/20200818111247_1498932_466_543.png',
    'https://cdnweb01.wikitree.co.kr/webdata/editor/202103/26/img_20210326134011_e2b92738.webp',
    'https://thumb.mt.co.kr/06/2020/09/2020091721371348318_1.jpg/dims/optimize/',
    'https://cdn.mhnse.com/news/photo/202105/74183_46752_4236.jpg',
    'https://img.hankyung.com/photo/202110/BF.27860017.1.jpg',
    'https://cdn.spotvnews.co.kr/news/photo/202107/430637_545462_1008.jpg',
    'data:image/jpeg;base64,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',
    'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTbONdenMtWMZNG7LKWzXGjhnDRpgKC0UYSAg&usqp=CAU',
    'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTtZpbIzwQCDgMulUXYrE1mZjmLqsNcb2-78Q&usqp=CAU',
    'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSFsK0GzQkPI5nZCrlOskKeIZSdWDNDCLD2Rg&usqp=CAU',
    'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRuhxeUbLxrmt0bzUj1RDM_dWAumEWMmcZJPA&usqp=CAU',
    'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRs1sW00xrpmGJCunk2Kimb-iJDhzj-PL8REg&usqp=CAU',
    'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcScLN1iHuHRS3FzRbDZKVSpfJEk_kyB8JhQvw&usqp=CAU',
    'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQFEM6-agP_n8F7ZAFbCmkKX4znvJhq3dpgAg&usqp=CAU',
    'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRbMjHc4l2ogUkI1VcMFwuKoOtafcajgui4DA&usqp=CAU',
    'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQzbiRDyOXgncS8boetPPpxTQsmzKfkjjccfg&usqp=CAU',
    'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQysjX5RoPilSdwUVTArCrtTvhWNei0U9-A4w&usqp=CAU',
    'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSvEwNCPHeuhVYMu3TkEnKpns3o-VWcjzDRYw&usqp=CAU',
    'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTTO4iAYXJpmYMHFbGuN6LBX15q5hEV_csc2A&usqp=CAU',
    'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ6uI3WnTSfj2DeN4avtcWeAOz1SDE1qnJcQQ&usqp=CAU',
  ];
  const [movies, setMovies] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const getMovies = async () => {
    const {
      data: {
        data: { movies: res },
      },
    } = await axios.get(
      'https://yts.mx/api/v2/list_movies.json?sort_by=rating&limit=50',
    );
    console.log(res);
    setMovies(() => [...res]);
    console.log(movies);
    setIsLoading(false);
  };
  useEffect(() => {
    getMovies();
  }, []);
  return (
    <Container>
      {isLoading ? (
        <span>Loading... </span>
      ) : (
        <>
          <Post
            key={998}
            title="예시그림2"
            poster={
              'https://i.pinimg.com/474x/b9/ae/a0/b9aea098ff88274d39f55b27d9c53ca0.jpg'
            }
          />
          <Post
            key={999}
            title="예시그림"
            poster={
              'https://i.pinimg.com/474x/ab/09/71/ab09717b28a5231c548c6c24736cd9a8.jpg'
            }
          />

          {dummy.map((movie, idx) => (
            <Post key={idx} title={123123123} poster={movie} />
          ))}
        </>
      )}
    </Container>
  );
}

export default Masonry;
