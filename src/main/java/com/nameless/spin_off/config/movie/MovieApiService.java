package com.nameless.spin_off.config.movie;

import com.nameless.spin_off.dto.MovieDto.NaverMoviesResponseDto;
import com.nameless.spin_off.dto.MovieDto.NaverMoviesResponseDto.NaverMovie;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.exception.movie.CantCorrectMovieUrlException;
import com.nameless.spin_off.repository.movie.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.nameless.spin_off.entity.enums.movie.MovieApiEnum.API_REQUEST_LENGTH_MAX;
import static com.nameless.spin_off.entity.enums.movie.MovieApiEnum.API_REQUEST_NUMBER_MAX;

@Slf4j
@RequiredArgsConstructor
@Service
public class MovieApiService {

    private final MovieRepository movieRepository;
    private final RestTemplate restTemplate;

    @Value("${spring.social.naver.client_id}")
    private String naverClientId;
    @Value("${spring.social.naver.client_secret}")
    private String naverClientSecret;
    @Value("${kobis.key}")
    private String key;
    private String kobisUrl = "http://kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieList.json";
    private String kobisInfoUrl = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieInfo.json";
    private String naverUrl = "https://openapi.naver.com/v1/search/movie.json";

    public List<Movie> findAllNew() {

        List<Movie> newMovieList = new ArrayList<>();
        int startPage = 1;
        for (int curPage = 0; curPage < API_REQUEST_NUMBER_MAX.getValue(); curPage++) {
            try {
                HttpHeaders header = new HttpHeaders();
                HttpEntity<?> entity = new HttpEntity<>(header);

                UriComponents uri = UriComponentsBuilder
                        .fromHttpUrl(
                                kobisUrl + "?" +
                                        "key=" + key + "&" +
                                        "itemPerPage=" + API_REQUEST_LENGTH_MAX.getValue() + "&" +
                                        "curPage=" + startPage + curPage)
                        .build();

                //이 한줄의 코드로 API를 호출해 MAP타입으로 전달 받는다.
                ResponseEntity<Map> resultMap = restTemplate.exchange(uri.toString(), HttpMethod.GET, entity, Map.class);

                LinkedHashMap lm = (LinkedHashMap)resultMap.getBody().get("movieListResult");
                ArrayList<Map> movieList = (ArrayList<Map>)lm.get("movieList");
                List<Movie> newPartMovieList = new ArrayList<>();

                for (Map obj : movieList) {
                    ArrayList<Map> directors = (ArrayList<Map>) (obj.get("directors"));
                    String director = getDirector(directors);


                    Movie movie = Movie.createMovie(Long.parseLong(String.valueOf(obj.get("movieCd"))),
                            String.valueOf(obj.get("movieNm")),
                            Integer.parseInt(String.valueOf(obj.get("prdtYear"))),
                            director, String.valueOf(obj.get("repNationNm")));

                    List<String> genres = Arrays.asList(String.valueOf(obj.get("genreAlt")).split(","));

                    movie.updateGenres(genres);
                    newPartMovieList.add(movie);
                }

                List<Long> movieIds = newPartMovieList.stream().map(Movie::getId).collect(Collectors.toList());

                List<Long> allIdsById = movieRepository.findAllIdsById(movieIds);

                List<Movie> resultMovieList = newPartMovieList
                        .stream()
                        .filter(m -> m.getDirectorName() != null)
                        .filter(m -> !allIdsById.contains(m.getId()))
                        .collect(Collectors.toList());

                if (resultMovieList.isEmpty()) {
                    break;
                }

                newMovieList.addAll(resultMovieList);

            } catch (HttpClientErrorException | HttpServerErrorException e) {
                log.info("kobisMovieError");
                log.info("statusCode : {}", e.getRawStatusCode());
                log.info("body : {}", e.getStatusText());
            } catch (Exception e) {
                log.debug("kobisMovieError");
                log.debug("예상하지 못한 에러, 데이터 오류 : {}", e.toString());
            }
        }

        return newMovieList;
    }

    public void updateActorsMovie(Movie movie) {
        try {
            HttpHeaders header = new HttpHeaders();
            HttpEntity<?> entity = new HttpEntity<>(header);

            UriComponents uri = UriComponentsBuilder
                    .fromHttpUrl(
                            kobisInfoUrl + "?" +
                                    "key=" + key + "&" +
                                    "movieCd=" + movie.getId())
                    .build();

            //이 한줄의 코드로 API를 호출해 MAP타입으로 전달 받는다.
            ResponseEntity<Map> resultMap = restTemplate.exchange(uri.toString(), HttpMethod.GET, entity, Map.class);

            LinkedHashMap lm = (LinkedHashMap)resultMap.getBody().get("movieListResult");
            ArrayList<Map> movieList = (ArrayList<Map>)lm.get("movieList");
            List<Movie> newPartMovieList = new ArrayList<>();

            for (Map obj : movieList) {
                ArrayList<Map> directors = (ArrayList<Map>) (obj.get("directors"));
                String director = getDirector(directors);


                Movie movie = Movie.createMovie(Long.parseLong(String.valueOf(obj.get("movieCd"))),
                        String.valueOf(obj.get("movieNm")),
                        Integer.parseInt(String.valueOf(obj.get("prdtYear"))),
                        director, String.valueOf(obj.get("repNationNm")));

                List<String> genres = Arrays.asList(String.valueOf(obj.get("genreAlt")).split(","));

                movie.updateGenres(genres);
                newPartMovieList.add(movie);
            }

            List<Long> movieIds = newPartMovieList.stream().map(Movie::getId).collect(Collectors.toList());

            List<Long> allIdsById = movieRepository.findAllIdsById(movieIds);

            List<Movie> resultMovieList = newPartMovieList
                    .stream()
                    .filter(m -> m.getDirectorName() != null)
                    .filter(m -> !allIdsById.contains(m.getId()))
                    .collect(Collectors.toList());

            if (resultMovieList.isEmpty()) {
                break;
            }

            newMovieList.addAll(resultMovieList);

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.info("kobisMovieError");
            log.info("statusCode : {}", e.getRawStatusCode());
            log.info("body : {}", e.getStatusText());
        } catch (Exception e) {
            log.debug("kobisMovieError");
            log.debug("예상하지 못한 에러, 데이터 오류 : {}", e.toString());
        }
    }

    public void updateThumbnailAndUrlByMovie(Movie movie) {
        try {
            HttpHeaders headers = new HttpHeaders(); // 헤더에 key들을 담아준다.
            headers.set("X-Naver-Client-Id", naverClientId);
            headers.set("X-Naver-Client-Secret", naverClientSecret);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            UriComponents uri = UriComponentsBuilder
                    .fromHttpUrl(
                            naverUrl + "?" +
                                    "query=" + movie.getTitle() + "&" +
                                    "yearfrom=" + movie.getYear() + "&" +
                                    "yearto=" + movie.getYear())
                    .build();

            NaverMoviesResponseDto response =
                    restTemplate.exchange(uri.toString(), HttpMethod.GET, entity,
                            NaverMoviesResponseDto.class, movie.getTitle()).getBody();

            assert response != null;
            TimeUnit.MILLISECONDS.sleep(500);
            if (response.getDisplay() == 1) {
                log.debug("one naver movie");
                log.debug("movie Id : {}", movie.getId());
                log.debug("movie Title : {}", movie.getTitle());
                NaverMovie naverMovie = response.getItems().get(0);

                if (movie.getTitle().replace(" ", "").equals(naverMovie.getTitle().replace("<b>", "").replace("</b>", "").replace(" ", "")) &&
                        movie.getDirectorName().contains(List.of(naverMovie.getDirector().split("\\|")).get(0))) {

                    log.debug("movie link : {}", naverMovie.getLink());
                    log.debug("movie image : {}", naverMovie.getImage());

                    movie.updateNaverUrl(naverMovie.getLink());
                    movie.updateImageUrl(naverMovie.getImage());
                } else {
                    throw new CantCorrectMovieUrlException();
                }

            } else if (response.getDisplay() > 1) {
                log.debug("multi movie");
                log.debug("movie Id : {}", movie.getId());
                log.debug("movie Title : {}", movie.getTitle());

                NaverMovie naverMovie = response.getItems()
                        .stream()
                        .filter(i -> movie.getTitle().replace(" ", "").equals(i.getTitle().replace("<b>", "").replace("</b>", "").replace(" ", "")))
                        .filter(i -> movie.getDirectorName().contains(List.of(i.getDirector().split("\\|")).get(0)))
                        .findFirst().orElseThrow(CantCorrectMovieUrlException::new);

                log.debug("movie link : {}", naverMovie.getLink());
                log.debug("movie image : {}", naverMovie.getImage());

                movie.updateNaverUrl(naverMovie.getLink());
                movie.updateImageUrl(naverMovie.getImage());
            } else {
                log.info("cant find naver movie");
                log.info("movie Id : {}", movie.getId());
                log.info("movie Title : {}", movie.getTitle());
            }
        } catch (CantCorrectMovieUrlException e) {
            log.info("cant find movie url");
            log.info("movie Id : {}", movie.getId());
            log.info("movie Title : {}", movie.getTitle());
        }catch (HttpClientErrorException | HttpServerErrorException e) {
            log.info("naverMovieError");
            log.info("statusCode : {}", e.getRawStatusCode());
            log.info("body : {}", e.getStatusText());
        } catch (Exception e) {
            log.error("naverMovieError");
            log.error("예상하지 못한 에러 : {}", e.toString());
        }
    }

    private String getDirector(ArrayList<Map> directors) {
        if (!directors.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Map map : directors) {
                stringBuilder.append(String.valueOf(map.get("peopleNm")));
                stringBuilder.append("/");
            }
            stringBuilder.setLength(stringBuilder.length() - 1);
            return stringBuilder.toString();
        } else {
            return null;
        }
    }
}
