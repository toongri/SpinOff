package com.nameless.spin_off.service.post;

import com.nameless.spin_off.dto.PostDto.CreatePostVO;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.entity.enums.post.PublicOfPostStatus;
import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.post.LikedPost;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.entity.post.ViewedPostByIp;
import com.nameless.spin_off.exception.collection.AlreadyCollectedPostException;
import com.nameless.spin_off.exception.collection.NotMatchCollectionException;
import com.nameless.spin_off.exception.hashtag.IncorrectHashtagContentException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.movie.NotExistMovieException;
import com.nameless.spin_off.exception.post.*;
import com.nameless.spin_off.exception.security.DontHaveAuthorityException;
import com.nameless.spin_off.repository.collection.CollectionRepository;
import com.nameless.spin_off.repository.hashtag.HashtagRepository;
import com.nameless.spin_off.repository.movie.MovieRepository;
import com.nameless.spin_off.repository.post.LikedPostRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.repository.post.ViewedPostByIpRepository;
import com.nameless.spin_off.repository.query.MemberQueryRepository;
import com.nameless.spin_off.repository.query.PostQueryRepository;
import com.nameless.spin_off.service.support.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.nameless.spin_off.entity.enums.ContentsLengthEnum.HASHTAG_LIST_MAX;
import static com.nameless.spin_off.entity.enums.ContentsLengthEnum.POST_IMAGE_MAX;
import static com.nameless.spin_off.entity.enums.ContentsTimeEnum.VIEWED_BY_IP_MINUTE;
import static com.nameless.spin_off.entity.enums.hashtag.HashtagCondition.CONTENT;
import static com.nameless.spin_off.entity.enums.post.PostScoreEnum.POST_VIEW;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostServiceJpa implements PostService{

    private final PostRepository postRepository;
    private final MovieRepository movieRepository;
    private final HashtagRepository hashtagRepository;
    private final CollectionRepository collectionRepository;
    private final PostQueryRepository postQueryRepository;
    private final LikedPostRepository likedPostRepository;
    private final ViewedPostByIpRepository viewedPostByIpRepository;
    private final AwsS3Service awsS3Service;
    private final MemberQueryRepository memberQueryRepository;

    @Transactional
    @Override
    public Long insertPostByPostVO(CreatePostVO postVO, Long memberId, List<MultipartFile> multipartFiles)
            throws NotExistMemberException, NotExistMovieException, IncorrectHashtagContentException,
            AlreadyPostedHashtagException, AlreadyCollectedPostException,
            IncorrectTitleOfPostException, IncorrectContentOfPostException, NotMatchCollectionException, IOException {

        List<String> urls = getUrlByMultipartFile(multipartFiles);

        List<Collection> collections = getCollections(postVO.getCollectionIds());
        isCorrectCollectionWithOwner(collections, memberId);

        List<Hashtag> hashtags = saveHashtagsByString(postVO.getHashtagContents());

        Movie movie = getMovieById(postVO.getMovieId());

        Post post = postRepository.save(Post.buildPost()
                .setMember(Member.createMember(memberId))
                .setPostPublicStatus(postVO.getPublicOfPostStatus())
                .setUrls(urls)
                .setHashTags(hashtags)
                .setThumbnailUrl(getThumbnails(urls))
                .setMovie(movie)
                .setTitle(postVO.getTitle())
                .setContent(postVO.getContent())
                .build());

        post.addAllCollectedPost(collections);

        return post.getId();
    }

    @Transactional
    @Override
    public Long insertLikedPostByMemberId(Long memberId, Long postId)
            throws NotExistMemberException, NotExistPostException, AlreadyLikedPostException {

        hasAuthPost(memberId, postId, getPublicOfPost(postId));
        isExistLikedPost(memberId, postId);
        return likedPostRepository.save(
                LikedPost.createLikedPost(Member.createMember(memberId), Post.createPost(postId))).getId();
    }

    @Transactional
    @Override
    public List<Long> insertCollectedPosts(Long memberId, Long postId, List<Long> collectionIds)
            throws NotMatchCollectionException,
            NotExistPostException, AlreadyCollectedPostException {

        hasAuthPost(memberId, postId, getPublicOfPost(postId));
        List<Collection> collections = getCollections(collectionIds);
        isCorrectCollectionWithOwner(collections, memberId);

        Post post = findOneByIdWithCollectedPost(postId);

        return post.insertCollectedPostByCollections(collections);
    }

    @Transactional
    @Override
    public Long insertViewedPostByIp(String ip, Long postId) throws NotExistPostException {

        if (!isExistPostIp(postId, ip)) {
            return viewedPostByIpRepository.
                    save(ViewedPostByIp.createViewedPostByIp(ip, Post.createPost(postId))).getId();
        } else {
            return null;
        }
    }

    @Transactional
    @Override
    public int updateAllPopularity() {
        List<Post> posts = postQueryRepository
                .findAllByViewAfterTime(POST_VIEW.getOldestDate());

        for (Post post : posts) {
            post.updatePopularity();
        }
        return posts.size();
    }

    private List<Hashtag> saveHashtagsByString(List<String> hashtagContents) throws IncorrectHashtagContentException {

        List<String> hashtagStrings = hashtagContents.stream()
                .distinct()
                .limit(HASHTAG_LIST_MAX.getLength())
                .collect(Collectors.toList());

        isNotContainCantChar(hashtagStrings);

        List<Hashtag> alreadySavedHashtags = hashtagRepository.findAllByContentIn(hashtagStrings);

        List<String> contentsAboutAlreadySavedHashtags =
                alreadySavedHashtags.stream().map(Hashtag::getContent).collect(Collectors.toList());

        List<Hashtag> anotherTags = hashtagStrings.stream()
                .filter(tag -> !contentsAboutAlreadySavedHashtags.contains(tag))
                .map(Hashtag::createHashtag)
                .collect(Collectors.toList());

        hashtagRepository.saveAll(anotherTags);

        alreadySavedHashtags.addAll(anotherTags);

        return alreadySavedHashtags;
    }

    private void isNotContainCantChar(List<String> hashtagContents) {
        for (String hashtagContent : hashtagContents) {
            if (CONTENT.isNotCorrect(hashtagContent)) {
                throw new IncorrectHashtagContentException(ErrorEnum.INCORRECT_HASHTAG_CONTENT);
            }
        }
    }

    private Movie getMovieById(Long movieId) throws NotExistMovieException {
        if (movieId == null) {
            return null;
        }
        Optional<Movie> optionalMovie = movieRepository.findById(movieId);

        return optionalMovie.orElseThrow(() -> new NotExistMovieException(ErrorEnum.NOT_EXIST_MOVIE));
    }

    private void isExistLikedPost(Long memberId, Long postId) {
        if (postQueryRepository.isExistLikedPost(memberId, postId)) {
            throw new AlreadyLikedPostException(ErrorEnum.ALREADY_LIKED_POST);
        }
    }

    private PublicOfPostStatus getPublicOfPost(Long postId) {
        return postQueryRepository.findPublicByPostId(postId)
                .orElseThrow(() -> new NotExistPostException(ErrorEnum.NOT_EXIST_POST));
    }

    private boolean isExistPostIp(Long postId, String ip) {
        return postQueryRepository.isExistIp(postId, ip, VIEWED_BY_IP_MINUTE.getDateTime());
    }

    public void isCorrectCollectionWithOwner(List<Collection> collections, Long memberId) {
        if (!collections.stream()
                .map(collection -> collection.getMember().getId()).allMatch(memberId1 -> memberId1.equals(memberId))) {
            throw new NotMatchCollectionException(ErrorEnum.NOT_MATCH_COLLECTION);
        }
    }

    private void hasAuthPost(Long memberId, Long postId, PublicOfPostStatus publicOfPostStatus) {
        if (publicOfPostStatus.equals(PublicOfPostStatus.A)) {
            if (postQueryRepository.isBlockMembersPost(memberId, postId)) {
                throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
            }
        } else if (publicOfPostStatus.equals(PublicOfPostStatus.C)){
            if (!postQueryRepository.isFollowMembersPost(memberId, postId)) {
                throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
            }
        } else if (publicOfPostStatus.equals(PublicOfPostStatus.B)){
            if (!memberId.equals(postQueryRepository.findOwnerIdByPostId(postId))) {
                throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
            }
        }
    }

    private Post findOneByIdWithCollectedPost(Long postId) {
        return postRepository.findOneByIdWithCollectedPost(postId)
                .orElseThrow(() -> new NotExistPostException(ErrorEnum.NOT_EXIST_POST));
    }

    private List<Collection> getCollections(List<Long> collectionIds) {
        List<Collection> collections = collectionRepository.findAllByIdIn(collectionIds);
        if (collections.size() != collectionIds.size()) {
            throw new NotMatchCollectionException(ErrorEnum.NOT_MATCH_COLLECTION);
        }
        return collections;
    }

    private String getThumbnails(List<String> urls) {
        return urls.isEmpty() ? null : urls.get(0);
    }

    private List<String> getUrlByMultipartFile(List<MultipartFile> multipartFiles) throws IOException {

        if (multipartFiles.size() > POST_IMAGE_MAX.getLength()) {
            throw new IncorrectPostImageLengthException(ErrorEnum.INCORRECT_POST_IMAGE_LENGTH);
        } else if (multipartFiles.isEmpty()) {
            return Collections.emptyList();
        } else {
            List<String> urls = new ArrayList<>();
            for (MultipartFile multipartFile : multipartFiles) {
                urls.add(awsS3Service.upload(multipartFile, "post"));
            }
            return urls;
        }
    }
}
