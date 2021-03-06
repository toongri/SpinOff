package com.nameless.spin_off.config.movie;

import com.nameless.spin_off.dto.MovieDto.NaverMoviesResponseDto;
import com.nameless.spin_off.dto.MovieDto.NaverMoviesResponseDto.NaverMovie;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.enums.ContentsBanKeywordEnum;
import com.nameless.spin_off.exception.movie.CantFindMovieUrlException;
import com.nameless.spin_off.exception.movie.OverKobisMovieApiLimitException;
import com.nameless.spin_off.exception.movie.UnKnownKobisException;
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

import static com.nameless.spin_off.enums.movie.MovieApiEnum.KOBIS_API_REQUEST_SIZE_MAX;

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
    @Value("${kobis.batchKey}")
    private String batchKey;
    @Value("${kobis.apiKey}")
    private String apiKey;
    private String key;
    private String kobisUrl = "http://kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieList.json";
    private String kobisInfoUrl = "http://www.kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieInfo.json";
    private String naverUrl = "https://openapi.naver.com/v1/search/movie.json";

    public List<Movie> findAllNew(int startPage, int size, boolean isBatch) {

        key = isBatch ? batchKey : apiKey;
        int errCnt = 0;
        List<Movie> newMovieList = new ArrayList<>();
        for (int curPage = 0; curPage < size; curPage++) {
            try {
                HttpHeaders header = new HttpHeaders();
                HttpEntity<?> entity = new HttpEntity<>(header);

                UriComponents uri = UriComponentsBuilder
                        .fromHttpUrl(
                                kobisUrl + "?" +
                                        "key=" + key + "&" +
                                        "itemPerPage=" + KOBIS_API_REQUEST_SIZE_MAX.getValue() + "&" +
                                        "curPage=" + (startPage + curPage))
                        .build();

                //??? ????????? ????????? API??? ????????? MAP???????????? ?????? ?????????.
                ResponseEntity<Map> resultMap = restTemplate.exchange(uri.toString(), HttpMethod.GET, entity, Map.class);
                log.debug("resultMap : {}", resultMap);
                if (resultMap.getBody().containsKey("faultInfo")) {
                    LinkedHashMap lm = (LinkedHashMap) resultMap.getBody().get("faultInfo");
                    if (String.valueOf(lm.get("errorCode")).equals("320099")) {
                        throw new UnKnownKobisException();
                    } else {
                        throw new OverKobisMovieApiLimitException();
                    }
                }

                LinkedHashMap lm = (LinkedHashMap) resultMap.getBody().get("movieListResult");
                ArrayList<Map> movieList = (ArrayList<Map>) lm.get("movieList");
                List<Movie> newPartMovieList = new ArrayList<>();

                for (Map obj : movieList) {
                    try {
                        ArrayList<Map> directors = (ArrayList<Map>) (obj.get("directors"));
                        String director = getDirector(directors);

                        Movie movie = Movie.createMovie(Long.parseLong(String.valueOf(obj.get("movieCd"))),
                                String.valueOf(obj.get("movieNm")),
                                Integer.parseInt(String.valueOf(obj.get("prdtYear"))),
                                director, String.valueOf(obj.get("repNationNm")));

                        log.debug("movie id : {}", movie.getId());
                        log.debug("movie title : {}", movie.getTitle());
                        String genreAlt = String.valueOf(obj.get("genreAlt"));
                        if (genreAlt.isEmpty()) {
                            continue;
                        }

                        List<String> genres = Arrays.asList(String.valueOf(obj.get("genreAlt")).split(","));
                        log.debug("movie genre : {}", genres);
                        if (genres.stream().anyMatch(ContentsBanKeywordEnum.MOVIE::isIn)) {
                            continue;
                        }

                        movie.updateGenres(genres);
                        newPartMovieList.add(movie);

                    } catch (NumberFormatException e) {
                        log.debug("kobisMovieDataError");
                        log.debug("????????? ?????? : {}", e.toString());
                    } catch (Exception e) {
                        log.error("kobisMovieError");
                        log.error("???????????? ?????? ?????? : {}", e.toString());
                    }
                }

                List<Long> movieIds = newPartMovieList.stream().map(Movie::getId).collect(Collectors.toList());
                log.debug("movieIds : {}", movieIds.stream().toString());

                List<Long> alreadyMovieIds = movieRepository.findAllIdsById(movieIds);
                log.debug("alreadyMovieIds : {}", alreadyMovieIds.stream().toString());

                List<Movie> resultMovieList = newPartMovieList
                        .stream()
                        .filter(m -> m.getDirectorName() != null && !alreadyMovieIds.contains(m.getId()))
                        .collect(Collectors.toList());
                log.debug("resultMovieList : {}", resultMovieList.stream().toString());

                if (!resultMovieList.isEmpty()) {
                    newMovieList.addAll(resultMovieList);
                }
                errCnt = 0;
            } catch (HttpClientErrorException | HttpServerErrorException e) {
                log.error("kobisMovieError");
                log.error("statusCode : {}", e.getRawStatusCode());
                log.error("body : {}", e.getStatusText());
                errCnt = 0;
            } catch (OverKobisMovieApiLimitException e) {
                log.error("OverKobisMovieApiLimitError");
                log.error("code : {}", e.getErrorEnum().getCode());
                log.error("message : {}", e.getMessage());
                errCnt = 0;
            } catch (UnKnownKobisException e) {
                log.error("UnKnownKobisException");
                if (errCnt < 10) {
                    errCnt++;
                    curPage--;
                } else {
                    errCnt = 0;
                }
            } catch (Exception e) {
                log.error("kobisMovieError");
                log.error("???????????? ?????? ?????? : {}", e.toString());
                errCnt = 0;
            }
        }
        log.info("newMovieList size : {}", newMovieList.size());
        return newMovieList;
    }

    public boolean updateActorsMovie(Movie movie, boolean isBatch) {
        key = isBatch ? batchKey : apiKey;
        for (int i = 0; i < 10; i++) {
            try {
                HttpHeaders header = new HttpHeaders();
                HttpEntity<?> entity = new HttpEntity<>(header);

                UriComponents uri = UriComponentsBuilder
                        .fromHttpUrl(
                                kobisInfoUrl + "?" +
                                        "key=" + key + "&" +
                                        "movieCd=" + movie.getId())
                        .build();

                //??? ????????? ????????? API??? ????????? MAP???????????? ?????? ?????????.
                ResponseEntity<Map> resultMap = restTemplate.exchange(uri.toString(), HttpMethod.GET, entity, Map.class);
                log.debug("actor resultMap : {}", resultMap);
                if (resultMap.getBody().containsKey("faultInfo")) {

                    LinkedHashMap lm = (LinkedHashMap) resultMap.getBody().get("faultInfo");
                    if (String.valueOf(lm.get("errorCode")).equals("320099")) {
                        throw new UnKnownKobisException();
                    } else {
                        throw new OverKobisMovieApiLimitException();
                    }
                }
                LinkedHashMap lm = (LinkedHashMap)resultMap.getBody().get("movieInfoResult");
                Map movieInfo = (Map)lm.get("movieInfo");

                ArrayList<Map> actors = (ArrayList<Map>) (movieInfo.get("actors"));
                movie.updateActors(getActors(actors));

                return true;
            } catch (HttpClientErrorException | HttpServerErrorException e) {
                log.error("kobisInfoMovieError");
                log.error("statusCode : {}", e.getRawStatusCode());
                log.error("body : {}", e.getStatusText());
                return false;
            } catch (OverKobisMovieApiLimitException e) {
                log.error("OverKobisMovieApiLimitError");
                log.error("code : {}", e.getErrorEnum().getCode());
                log.error("message : {}", e.getMessage());
                return false;
            } catch (UnKnownKobisException e) {
                log.error("UnKnownKobisException");
            } catch (NumberFormatException e) {
                log.debug("kobisInfoMovieDataError");
                log.debug("????????? ?????? : {}", e.toString());
                return false;
            }  catch (Exception e) {
                log.error("kobisInfoMovieError");
                log.error("???????????? ?????? ?????? : {}", e.toString());
                return false;
            }
        }
        return false;
    }

    public boolean updateThumbnailAndUrlByMovie(Movie movie) {
        try {
            HttpHeaders headers = new HttpHeaders(); // ????????? key?????? ????????????.
            headers.set("X-Naver-Client-Id", naverClientId);
            headers.set("X-Naver-Client-Secret", naverClientSecret);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            UriComponents uri = UriComponentsBuilder
                    .fromHttpUrl(
                            naverUrl + "?" +
                                    "query=" + movie.getTitle() + "&" +
                                    "yearfrom=" + (movie.getYear() - 1) + "&" +
                                    "yearto=" + (movie.getYear() + 1))
                    .build();

            NaverMoviesResponseDto response =
                    restTemplate.exchange(uri.toString(), HttpMethod.GET, entity,
                            NaverMoviesResponseDto.class, movie.getTitle()).getBody();

            assert response != null;
            TimeUnit.MILLISECONDS.sleep(700);

            if (response.getDisplay() == 1) {
                log.debug("one naver movie");
                log.debug("movie Id : {}", movie.getId());
                log.debug("movie Title : {}", movie.getTitle());
                NaverMovie naverMovie = response.getItems().get(0);

                if (movie.getTitle().length() >= 4) {
                    if (movie.getTitle().replace(" ", "").equals(naverMovie.getTitle()
                            .replace("<b>", "").replace("</b>", "")
                            .replace(" ", ""))) {

                        log.debug("movie link : {}", naverMovie.getLink());
                        log.debug("movie image : {}", naverMovie.getImage());

                        movie.updateNaverUrl(naverMovie.getLink());
                        movie.updateThumbnail(naverMovie.getImage());
                    } else {
                        throw new CantFindMovieUrlException();
                    }
                } else {
                    if (movie.getTitle().replace(" ", "").equals(naverMovie.getTitle()
                        .replace("<b>", "").replace("</b>", "")
                        .replace(" ", "")) && isContains(movie, naverMovie)) {

                        if (movie.getNaverUrl() == naverMovie.getLink() && movie.getThumbnail() == naverMovie.getImage()) {
                            return false;
                        }

                        log.debug("movie link : {}", naverMovie.getLink());
                        log.debug("movie image : {}", naverMovie.getImage());

                        movie.updateNaverUrl(naverMovie.getLink());
                        movie.updateThumbnail(naverMovie.getImage());

                    } else {
                        throw new CantFindMovieUrlException();
                    }
                }

            } else if (response.getDisplay() > 1) {
                log.debug("multi movie");
                log.debug("movie Id : {}", movie.getId());
                log.debug("movie Title : {}", movie.getTitle());

                NaverMovie naverMovie;

                if (movie.getTitle().length() < 4) {
                    naverMovie = response.getItems()
                            .stream()
                            .filter(i -> movie.getTitle().replace(" ", "").equals(i.getTitle().replace("<b>", "").replace("</b>", "").replace(" ", "")))
                            .filter(i -> isContains(movie, i))
                            .findFirst().orElseThrow(CantFindMovieUrlException::new);
                } else {
                    naverMovie = response.getItems()
                            .stream()
                            .filter(i -> movie.getTitle().replace(" ", "").equals(i.getTitle().replace("<b>", "").replace("</b>", "").replace(" ", "")))
                            .findFirst().orElseThrow(CantFindMovieUrlException::new);
                }

                if (movie.getNaverUrl() == naverMovie.getLink() && movie.getThumbnail() == naverMovie.getImage()) {
                    return false;
                }

                log.debug("movie link : {}", naverMovie.getLink());
                log.debug("movie image : {}", naverMovie.getImage());

                movie.updateNaverUrl(naverMovie.getLink());
                movie.updateThumbnail(naverMovie.getImage());
            } else {
                throw new CantFindMovieUrlException();
            }
        } catch (CantFindMovieUrlException e) {
            log.debug("cant find movie url");
            log.debug("movie Id : {}", movie.getId());
            return false;
        }catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("naverMovieError");
            log.error("statusCode : {}", e.getRawStatusCode());
            log.error("body : {}", e.getStatusText());
            return false;
        } catch (NumberFormatException e) {
            log.error("naverMovieDataError");
            log.error("????????? ?????? : {}", e.toString());
            return false;
        }  catch (Exception e) {
            log.error("naverMovieError");
            log.error("???????????? ?????? ?????? : {}", e.toString());
            return false;
        }
        return true;
    }

    private String getActors(ArrayList<Map> actors) {
        if (!actors.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();
            int cnt = 0;
            for (Map map : actors) {
                stringBuilder.append(map.get("peopleNm"));
                String cast = String.valueOf(map.get("cast"));
                if (!cast.equals("")) {
                    stringBuilder.append(" - ");
                    stringBuilder.append(cast);
                }
                stringBuilder.append(", ");
                cnt++;
                if (cnt == 3) {
                    break;
                }
            }
            stringBuilder.setLength(stringBuilder.length() - 2);
            return stringBuilder.toString();
        } else {
            return null;
        }
    }

    private boolean isContains(Movie movie, NaverMovie i) {
        for (String str : List.of(i.getDirector().split("\\|"))) {
            if (movie.getDirectorName().replace(" ", "").contains(str.replace(" ", ""))) {
                return true;
            }
        }
        return false;
    }

    private String getDirector(ArrayList<Map> directors) {
        if (!directors.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Map map : directors) {
                stringBuilder.append(map.get("peopleNm"));
                stringBuilder.append("/");
            }
            stringBuilder.setLength(stringBuilder.length() - 1);
            return stringBuilder.toString();
        } else {
            return null;
        }
    }
}
