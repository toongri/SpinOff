import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Container } from './styles';
import Post from '../../atoms/Post';

function Masonry() {
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
            poster={'//s3-us-west-2.amazonaws.com/s.cdpn.io/4273/rapunzel.jpg'}
          />
          <Post
            key={999}
            title="예시그림"
            poster={
              '//s3-us-west-2.amazonaws.com/s.cdpn.io/4273/cinderella.jpg'
            }
          />

          {movies.map(movie => (
            <Post
              key={movie.id}
              title={movie.title}
              poster={movie.medium_cover_image}
            />
          ))}
        </>
      )}
    </Container>
  );
}

export default Masonry;
