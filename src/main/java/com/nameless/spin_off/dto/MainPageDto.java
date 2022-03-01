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
        Slice<MainPagePostDto> posts;
        Slice<MainPageCollectionDto> collections;
    }

    @Data
    @AllArgsConstructor
    public static class MainPageFollowDto {
        Slice<MainPagePostDto> postsOfFollowingMember;
        Slice<MainPagePostDto> postsOfFollowingHashtag;
        Slice<MainPagePostDto> postsOfFollowingMovie;
        Slice<MainPageCollectionDto> collectionOfFollowingCollection;
    }
}