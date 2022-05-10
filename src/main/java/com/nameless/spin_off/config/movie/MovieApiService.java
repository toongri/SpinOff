package com.nameless.spin_off.config.movie;

import com.nameless.spin_off.entity.movie.Movie;
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

@Service
public class MovieApiService {

    public List<Movie> findAllNew() {

        HashMap<String, Object> result = new HashMap<String, Object>();
        String jsonInString = "";
        List<Movie> newMovieList = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders header = new HttpHeaders();
            HttpEntity<?> entity = new HttpEntity<>(header);
            String url = "http://kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchDailyBoxOfficeList.json";

            UriComponents uri = UriComponentsBuilder.fromHttpUrl(url+"?"+"key=f5eef3421c602c6cb7ea224104795888&targetDt=20210802").build();

            //이 한줄의 코드로 API를 호출해 MAP타입으로 전달 받는다.
            ResponseEntity<Map> resultMap = restTemplate.exchange(uri.toString(), HttpMethod.GET, entity, Map.class);
            result.put("statusCode", resultMap.getStatusCodeValue()); //http status code를 확인
            result.put("header", resultMap.getHeaders()); //헤더 정보 확인
            result.put("body", resultMap.getBody()); //실제 데이터 정보 확인

            LinkedHashMap lm = (LinkedHashMap)resultMap.getBody().get("movieListResult");
            ArrayList<Map> movieList = (ArrayList<Map>)lm.get("movieList");

            for (Map obj : movieList) {
                ArrayList<Map> directors = (ArrayList<Map>) (obj.get("directors"));
                String director = getDirector(directors);

                Movie movie = Movie.createMovie((Long) obj.get("movieCd"), (String) obj.get("movieNm"),
                        (Integer) obj.get("prdtYear"), director, (String) obj.get("repNationNm"));

                List<String> genres = Arrays.asList(((String)(obj.get("genreAlt"))).split(","));

                movie.updateGenres(genres);
                newMovieList.add(movie);
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            result.put("statusCode", e.getRawStatusCode());
            result.put("body"  , e.getStatusText());
            System.out.println(e.toString());

        } catch (Exception e) {
            result.put("statusCode", "999");
            result.put("body"  , "excpetion오류");
            System.out.println(e.toString());
        }
        return newMovieList;
    }

    private String getDirector(ArrayList<Map> directors) {
        if (!directors.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Map map : directors) {
                stringBuilder.append((String)(map.get("peopleNm")));
                stringBuilder.append("/");
            }
            stringBuilder.setLength(stringBuilder.length() - 1);
            return stringBuilder.toString();
        } else {
            return null;
        }
    }
}
