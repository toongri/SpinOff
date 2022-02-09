package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.CollectionDto;
import com.nameless.spin_off.dto.CollectionDto.MainPageCollectionDto;
import com.nameless.spin_off.dto.PostDto;
import com.nameless.spin_off.dto.PostDto.MainPagePostDto;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.repository.query.MainPageQueryRepository;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaMainPageService implements MainPageService{

    private final MainPageQueryRepository mainPageQueryRepository;

    @Override
    public Slice<MainPagePostDto> getPostsOrderById(Pageable pageable) {
        Slice<Post> postSlice = mainPageQueryRepository.findPostsOrderByIdBySlicing(pageable);

        return postSlice.map(MainPagePostDto::new);
    }

    @Override
    public Slice<MainPagePostDto> getPostsOrderByPopularityBySlicingAfterLocalDateTime(
            Pageable pageable, LocalDateTime startDateTime, LocalDateTime endDateTime) {

        Slice<Post> postSlice = mainPageQueryRepository
                .findPostsOrderByPopularityBySlicingAfterLocalDateTime(pageable, startDateTime, endDateTime);

        return postSlice.map(MainPagePostDto::new);
    }

    @Override
    public Slice<MainPageCollectionDto> getCollectionsOrderByPopularityBySlicingAfterLocalDateTime(
            Pageable pageable, LocalDateTime startDateTime, LocalDateTime endDateTime) {

        Slice<Collection> collectionSlice = mainPageQueryRepository
                .findCollectionsOrderByPopularityBySlicingAfterLocalDateTime(pageable, startDateTime, endDateTime);

        return collectionSlice.map(MainPageCollectionDto::new);
    }
}
