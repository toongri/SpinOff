package com.nameless.spin_off.service.post;

import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.CollectionDto.PostThumbnailsCollectionDto;
import com.nameless.spin_off.dto.PostDto.CreatePostVO;
import com.nameless.spin_off.dto.PostDto.ThumbnailAndPublicPostDto;
import com.nameless.spin_off.entity.collection.CollectedPost;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.post.LikedPost;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.entity.post.PostedMedia;
import com.nameless.spin_off.entity.post.ViewedPostByIp;
import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.enums.post.PublicOfPostStatus;
import com.nameless.spin_off.exception.collection.AlreadyCollectedPostException;
import com.nameless.spin_off.exception.collection.NotMatchCollectionException;
import com.nameless.spin_off.exception.hashtag.IncorrectHashtagContentException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.movie.NotExistMovieException;
import com.nameless.spin_off.exception.post.*;
import com.nameless.spin_off.exception.security.DontHaveAuthorityException;
import com.nameless.spin_off.repository.collection.CollectedPostRepository;
import com.nameless.spin_off.repository.collection.CollectionRepository;
import com.nameless.spin_off.repository.hashtag.HashtagRepository;
import com.nameless.spin_off.repository.movie.MovieRepository;
import com.nameless.spin_off.repository.post.LikedPostRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.repository.post.ViewedPostByIpRepository;
import com.nameless.spin_off.repository.query.CollectionQueryRepository;
import com.nameless.spin_off.repository.query.PostQueryRepository;
import com.nameless.spin_off.service.support.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.nameless.spin_off.enums.ContentsLengthEnum.HASHTAG_LIST_MAX;
import static com.nameless.spin_off.enums.ContentsLengthEnum.POST_IMAGE_MAX;
import static com.nameless.spin_off.enums.ContentsTimeEnum.VIEWED_BY_IP_MINUTE;
import static com.nameless.spin_off.enums.post.PostScoreEnum.POST_VIEW;

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
    private final CollectionQueryRepository collectionQueryRepository;
    private final CollectedPostRepository collectedPostRepository;

    @Transactional
    @Override
    public Long insertPostByPostVO(CreatePostVO postVO, Long memberId, List<MultipartFile> multipartFiles)
            throws NotExistMemberException, NotExistMovieException, IncorrectHashtagContentException,
            AlreadyPostedHashtagException, AlreadyCollectedPostException,
            IncorrectTitleOfPostException, IncorrectContentOfPostException, NotMatchCollectionException, IOException {

        List<Long> collectionIds = postVO.getCollectionIds();
        List<String> urls = getUrlByMultipartFile(multipartFiles);

        isCorrectCollectionIds(collectionIds, memberId);

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

        if (!collectionIds.isEmpty()) {
            post.addAllCollectedPostByIds(collectionIds);
            collectionQueryRepository.updateCollectionsThumbnails(getThumbnails(urls), collectionIds);
        }
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
    public List<Long> updateCollectedPosts(Long memberId, Long postId, List<Long> collectionIds)
            throws NotMatchCollectionException,
            NotExistPostException, AlreadyCollectedPostException {

        ThumbnailAndPublicPostDto publicAndThumbnailOfPost = getPublicAndThumbnailOfPost(postId);
        hasAuthPost(memberId, postId, publicAndThumbnailOfPost.getPublicOfPostStatus());
        isCorrectCollectionIds(collectionIds, memberId);

        List<CollectedPost> collectedPosts = collectionQueryRepository.findAllIdByPostIdAndMemberId(postId, memberId);

        List<CollectedPost> collectForDelete = collectedPosts.stream()
                .filter(c -> !collectionIds.contains(c.getCollection().getId()))
                .collect(Collectors.toList());

        if (!collectForDelete.isEmpty()) {
            collectedPostRepository.deleteAll(collectForDelete);
            List<Long> collectIds = collectForDelete.stream().map(c -> c.getCollection().getId()).collect(Collectors.toList());

            Map<Long, List<PostThumbnailsCollectionDto>> thumbnails =
                    collectionQueryRepository.findPostThumbnailsByCollectionIds(collectIds)
                    .stream().collect(Collectors.groupingBy(PostThumbnailsCollectionDto::getCollectionId));

            for (Long collectId : collectIds) {
                collectionQueryRepository
                        .resetCollectionThumbnail(collectId, getPostThumbnailsCollectionDtos(thumbnails.get(collectId), collectId));
            }
        }

        List<Long> collectionIdsInPost = collectedPosts.stream()
                .map(c -> c.getCollection().getId())
                .collect(Collectors.toList());

        List<Long> addCollectionIds = collectionIds.stream()
                .filter(c -> !collectionIdsInPost.contains(c)).collect(Collectors.toList());

        if (addCollectionIds.isEmpty()) {
            return Collections.emptyList();
        } else {
            collectionQueryRepository.updateCollectionsThumbnails(publicAndThumbnailOfPost.getThumbnail(), addCollectionIds);
            return collectedPostRepository.saveAll(
                    addCollectionIds.stream()
                            .map(c -> CollectedPost
                                    .createCollectedPost(Collection.createCollection(c), Post.createPost(postId)))
                            .collect(Collectors.toList())).stream().map(CollectedPost::getId).collect(Collectors.toList());
        }
    }

    @Transactional
    @Override
    public Long insertCollectedPost(Long memberId, Long postId, Long collectionId) {
        hasAuthPost(memberId, postId, getPublicOfPost(postId));
        isCorrectCollectionId(memberId, collectionId);
        isExistCollectedPost(postId, collectionId);

        return collectedPostRepository.save(CollectedPost.createCollectedPost(
                Collection.createCollection(collectionId),
                Post.createPost(postId))).getId();
    }

    @Transactional
    @Override
    public Long deletePost(MemberDetails currentMember, Long postId) {
        Post post = getPostWithPostedMedia(postId);
        hasAuthPost(currentMember, post.getMember().getId());
        for (PostedMedia media : post.getPostedMedias()) {
            awsS3Service.deleteFile(media.getUrl());
        }
        postRepository.delete(post);
        return 1L;
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

    private Post getPostWithPostedMedia(Long postId) {
        return postRepository.findOneByIdWithPostedMedia(postId)
                .orElseThrow(() -> new NotExistPostException(ErrorEnum.NOT_EXIST_POST));
    }

    private List<Hashtag> saveHashtagsByString(List<String> hashtagContents) throws IncorrectHashtagContentException {

        List<String> hashtagStrings = hashtagContents.stream()
                .distinct()
                .limit(HASHTAG_LIST_MAX.getLength())
                .collect(Collectors.toList());

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

    private List<String> getPostThumbnailsCollectionDtos(List<PostThumbnailsCollectionDto> thumbnails, Long collectId) {
        if (thumbnails == null) {
            return Collections.emptyList();
        } else {
            return thumbnails.stream().map(PostThumbnailsCollectionDto::getPostThumbnail).collect(Collectors.toList());
        }
    }

    private void isExistCollectedPost(Long postId, Long collectionId) {
        if (postQueryRepository.isExistCollectedPost(postId, collectionId)) {
            throw new AlreadyCollectedPostException(ErrorEnum.ALREADY_COLLECTED_POST);
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

    private ThumbnailAndPublicPostDto getPublicAndThumbnailOfPost(Long postId) {
        return postQueryRepository.findThumbnailAndPublicByPostId(postId)
                .orElseThrow(() -> new NotExistPostException(ErrorEnum.NOT_EXIST_POST));
    }

    private boolean isExistPostIp(Long postId, String ip) {
        return postQueryRepository.isExistIp(postId, ip, VIEWED_BY_IP_MINUTE.getDateTimeMinusMinutes());
    }

    private void hasAuthPost(Long memberId, Long postId, PublicOfPostStatus publicOfPostStatus) {
        if (publicOfPostStatus.equals(PublicOfPostStatus.A)) {
            if (postQueryRepository.isBlockMembersPost(memberId, postId)) {
                throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
            }
        } else if (publicOfPostStatus.equals(PublicOfPostStatus.C)){
            if (!postQueryRepository.isFollowMembersOrOwnerPost(memberId, postId)) {
                throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
            }
        } else if (publicOfPostStatus.equals(PublicOfPostStatus.B)){
            if (!memberId.equals(postQueryRepository.findOwnerIdByPostId(postId).orElseGet(() -> null))) {
                throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
            }
        }
    }

    private void hasAuthPost(MemberDetails currentMember, Long memberId) {
        if (!(currentMember.isAdmin() || currentMember.getId().equals(memberId))) {
            throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
        }
    }

    private void isCorrectCollectionId(Long memberId, Long collectionId) {
        if (!memberId.equals(collectionQueryRepository.findOwnerIdByCollectionId(collectionId).orElseGet(() -> null))) {
            throw new NotMatchCollectionException(ErrorEnum.NOT_MATCH_COLLECTION);
        }
    }

    private void isCorrectCollectionIds(List<Long> collectionIds, Long memberId) {
        List<Long> collections = collectionRepository.findAllIdByIdIn(collectionIds, memberId);
        if (collections.size() != collectionIds.size()) {
            throw new NotMatchCollectionException(ErrorEnum.NOT_MATCH_COLLECTION);
        }
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
