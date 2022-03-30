package com.nameless.spin_off.config.auth;

import com.google.gson.Gson;
import com.nameless.spin_off.config.auth.dto.AccessToken;
import com.nameless.spin_off.config.auth.dto.OAuthRequest;
import com.nameless.spin_off.config.auth.dto.profile.GoogleProfile;
import com.nameless.spin_off.config.auth.dto.profile.KakaoProfile;
import com.nameless.spin_off.config.auth.dto.profile.NaverProfile;
import com.nameless.spin_off.config.auth.dto.profile.ProfileDto;
import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.exception.sign.SocialException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static com.nameless.spin_off.entity.enums.member.EmailLinkageServiceEnum.google;
import static com.nameless.spin_off.entity.enums.member.EmailLinkageServiceEnum.kakao;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProviderService {

    private final RestTemplate restTemplate;
    private final Gson gson;
    private final OAuthRequestFactory oAuthRequestFactory;

    public ProfileDto getProfile(String accessToken, String provider) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.set("Authorization", "Bearer " + accessToken);

        String profileUrl = oAuthRequestFactory.getProfileUrl(provider);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(null, httpHeaders);
        ResponseEntity<String> response = restTemplate.postForEntity(profileUrl, request, String.class);

        try {
            if (response.getStatusCode() == HttpStatus.OK) {
                return extractProfile(response, provider);
            }
        } catch (Exception e) {
            throw new SocialException(ErrorEnum.SOCIAL);
        }
        throw new SocialException(ErrorEnum.SOCIAL);
    }

    private ProfileDto extractProfile(ResponseEntity<String> response, String provider) {
        log.debug("response.getBody {}", response.getBody());
        if (kakao.getName().equals(provider)) {
            KakaoProfile kakaoProfile = gson.fromJson(response.getBody(), KakaoProfile.class);
            return new ProfileDto(kakaoProfile.getKakao_account().getEmail(),
                    kakaoProfile.getKakao_account().getProfile().getNickname());
        } else if(google.getName().equals(provider)) {
            GoogleProfile googleProfile = gson.fromJson(response.getBody(), GoogleProfile.class);
            return new ProfileDto(googleProfile.getEmail(), googleProfile.getName());
        } else {
            NaverProfile naverProfile = gson.fromJson(response.getBody(), NaverProfile.class);
            return new ProfileDto(naverProfile.getResponse().getEmail(), naverProfile.getResponse().getName());
        }
    }

    public AccessToken getAccessToken(String code, String provider) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        OAuthRequest oAuthRequest = oAuthRequestFactory.getRequest(code, provider);
        HttpEntity<LinkedMultiValueMap<String, String>> request = new HttpEntity<>(oAuthRequest.getMap(), httpHeaders);
        log.debug("request : {}", request);

        ResponseEntity<String> response = restTemplate.postForEntity(oAuthRequest.getUrl(), request, String.class);
        log.debug("response : {}", response);
        try {
            if (response.getStatusCode() == HttpStatus.OK) {
                return gson.fromJson(response.getBody(), AccessToken.class);
            }
        } catch (Exception e) {
            throw new SocialException(ErrorEnum.SOCIAL);
        }
        throw new SocialException(ErrorEnum.SOCIAL);
    }
}