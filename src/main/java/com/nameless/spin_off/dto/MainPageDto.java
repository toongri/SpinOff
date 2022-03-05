package com.nameless.spin_off.dto;

import com.nameless.spin_off.dto.CollectionDto.MainPageCollectionDto;
import com.nameless.spin_off.dto.PostDto.MainPagePostDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Slice;

public class MainPageDto {

    @Data
    @AllArgsConstructor
    public static class MainPageDiscoveryDto {
        Slice<MainPagePostDto> postsByPopular;
        Slice<MainPagePostDto> postsById;
        Slice<MainPageCollectionDto> collections;
    }

    @Data
    @AllArgsConstructor
    public static class MainPageFollowDto {
        Slice<MainPagePostDto> postsByFollowedMember;
        Slice<MainPagePostDto> postsByFollowedHashtag;
        Slice<MainPagePostDto> postsByFollowedMovie;
        Slice<MainPageCollectionDto> collections;
    }
}
