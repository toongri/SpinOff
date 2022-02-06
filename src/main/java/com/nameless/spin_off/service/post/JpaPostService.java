package com.nameless.spin_off.service.post;

import com.nameless.spin_off.dto.PostDto.CreatePostVO;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.post.*;
import com.nameless.spin_off.exception.member.NoSuchMemberException;
import com.nameless.spin_off.exception.movie.NoSuchMovieException;
import com.nameless.spin_off.exception.post.NoSuchPostException;
import com.nameless.spin_off.exception.post.OverSuchViewedPostByIpException;
import com.nameless.spin_off.repository.collections.CollectionRepository;
import com.nameless.spin_off.repository.collections.VisitedCollectionByMemberRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.movie.MovieRepository;
import com.nameless.spin_off.repository.post.HashtagRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.repository.post.ViewedPostByIpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaPostService implements PostService{

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final MovieRepository movieRepository;
    private final HashtagRepository hashtagRepository;
    private final CollectionRepository collectionRepository;

    @Transactional(readOnly = false)
    @Override
    public Long savePostByPostVO(CreatePostVO postVO) throws NoSuchMemberException, NoSuchMovieException {

        Member member = getMemberById(postVO.getMemberId());

        List<PostedMedia> postedMedia = PostedMedia.createPostedMedias(postVO.getMediaUrls());

        List<Hashtag> hashtags = saveHashtagsByString(postVO.getHashtagContents());

        List<Movie> movies = getMoviesByIds(postVO.getMovieIds());

        Post post =  Post.buildPost()
                .setMember(member)
                .setPostPublicStatus(postVO.getPublicOfPostStatus())
                .setPostedMedias(postedMedia)
                .setHashTags(hashtags)
                .setMovies(movies)
                .setTitle(postVO.getTitle())
                .setContent(postVO.getContent())
                .build();

        List<Collection> collections = collectionRepository.findAllByIdIn(postVO.getCollectionIds());
        collections.forEach(collection -> collection.addCollectedPostByPost(post));

        return postRepository.save(post).getId();
    }

    @Transactional(readOnly = false)
    @Override
    public Post updateLikedPostByMemberId(Long memberId, Long postId) throws NoSuchMemberException, NoSuchPostException {
        Member member = getMemberById(memberId);
        Post post = getPostByIdWithLikedPost(postId);


        post.addLikedPostByMember(member);

        return post;
    }

    @Transactional(readOnly = false)
    @Override
    public Post updateViewedPostByIp(String ip, Long postId, LocalDateTime timeNow, Long minuteDuration)
            throws NoSuchPostException, OverSuchViewedPostByIpException {

        Post post = getPostByIdWithViewedIp(postId);

        Optional<ViewedPostByIp> optionalViewedPostByIp = getCollectByIpAndTime(ip, post, timeNow, minuteDuration);

        if (optionalViewedPostByIp.isEmpty()) {
            post.addViewedPostByIp(ip);
        }

        return post;
    }

    private List<LikedPost> getCollectByLikedPostAndMember(Member member, LocalDateTime timeNow, Long minuteDuration, Post post) {
        return post.getLikedPosts().stream()
                .filter(likedPost -> likedPost.getMember() == member)
                .collect(Collectors.toList());
    }

    private Optional<ViewedPostByIp> getCollectByIpAndTime(String ip, Post post, LocalDateTime timeNow, Long minuteDuration)
            throws OverSuchViewedPostByIpException {
        List<ViewedPostByIp> collect = post.getViewedPostByIps().stream()
                .filter(viewedPostByIp -> viewedPostByIp.getIp().equals(ip) &&
                        ChronoUnit.MINUTES.between(viewedPostByIp.getCreatedDate(), timeNow) < minuteDuration)
                .collect(Collectors.toList());

        int size = collect.size();

        if (size == 0) {
            return Optional.empty();
        } else if (size == 1) {
            return Optional.of(collect.get(0));
        } else {
            throw new OverSuchViewedPostByIpException();
        }

    }

    private List<Hashtag> saveHashtagsByString(List<String> hashtagContents) {
        List<Hashtag> alreadySavedHashtags = hashtagRepository.findAllByContentIn(hashtagContents);
        List<String> contentsAboutAlreadySavedHashtags =
                alreadySavedHashtags.stream().map(Hashtag::getContent).collect(Collectors.toList());

        List<Hashtag> anotherTags = getHashtagsAboutNotSaved(hashtagContents, contentsAboutAlreadySavedHashtags);
        hashtagRepository.saveAll(anotherTags);

        alreadySavedHashtags.addAll(anotherTags);

        return alreadySavedHashtags;
    }

    private List<Hashtag> getHashtagsAboutNotSaved(List<String> hashtagContents, List<String> contentsAboutAlreadySavedHashtags) {
        return hashtagContents.stream()
                .filter(tag -> !contentsAboutAlreadySavedHashtags.contains(tag))
                .map(Hashtag::createHashtag)
                .collect(Collectors.toList());
    }

    private Member getMemberById(Long memberId) throws NoSuchMemberException {
        Optional<Member> optionalMember = memberRepository.findById(memberId);

        return optionalMember.orElseThrow(NoSuchMemberException::new);
    }

    private List<Movie> getMoviesByIds(List<Long> movieIds) throws NoSuchMovieException {
        List<Movie> movies = movieRepository.findTagsByIdIn(movieIds);
        if (movies.size() == movieIds.size()) {
            return movies;
        } else {
            throw new NoSuchMovieException();
        }
    }

    private Post getPostByIdWithViewedIp(Long postId) throws NoSuchPostException {
        Optional<Post> optionalPost = postRepository.findOneByIdFetchJoinViewedPostByIpOrderByViewedIpId(postId);

        return optionalPost.orElseThrow(NoSuchPostException::new);
    }

    private Post getPostByIdWithLikedPost(Long postId) throws NoSuchPostException {
        Optional<Post> optionalPost = postRepository.findOneByIdFetchJoinLikedPost(postId);

        return optionalPost.orElseThrow(NoSuchPostException::new);
    }
}
