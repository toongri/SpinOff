package com.nameless.spin_off.service.help;

import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.comment.CommentInCollection;
import com.nameless.spin_off.entity.comment.CommentInPost;
import com.nameless.spin_off.entity.enums.help.ComplainStatus;
import com.nameless.spin_off.entity.enums.help.ContentTypeStatus;
import com.nameless.spin_off.entity.help.Complain;
import com.nameless.spin_off.entity.member.DirectMessage;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.exception.collection.NotExistCollectionException;
import com.nameless.spin_off.exception.comment.NotExistCommentInCollectionException;
import com.nameless.spin_off.exception.comment.NotExistCommentInPostException;
import com.nameless.spin_off.exception.help.UnknownContentTypeException;
import com.nameless.spin_off.exception.member.AlreadyComplainException;
import com.nameless.spin_off.exception.member.NotExistDMException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.post.NotExistPostException;
import com.nameless.spin_off.repository.collection.CollectionRepository;
import com.nameless.spin_off.repository.comment.CommentInCollectionRepository;
import com.nameless.spin_off.repository.comment.CommentInPostRepository;
import com.nameless.spin_off.repository.help.ComplainRepository;
import com.nameless.spin_off.repository.member.DirectMessageRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.repository.query.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.nameless.spin_off.entity.enums.help.ContentTypeStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ComplainServiceJpa implements ComplainService{

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CollectionRepository collectionRepository;
    private final DirectMessageRepository directMessageRepository;
    private final CommentInCollectionRepository commentInCollectionRepository;
    private final CommentInPostRepository commentInPostRepository;
    private final MemberQueryRepository memberQueryRepository;
    private final PostQueryRepository postQueryRepository;
    private final CollectionQueryRepository collectionQueryRepository;
    private final CommentInPostQueryRepository commentInPostQueryRepository;
    private final CommentInCollectionQueryRepository commentInCollectionQueryRepository;
    private final DirectMessageQueryRepository directMessageQueryRepository;
    private final ComplainQueryRepository complainQueryRepository;
    private final ComplainRepository complainRepository;


    @Transactional
    @Override
    public Long insertComplain(Long memberId, Long complainedId, ContentTypeStatus contentTypeStatus,
                               ComplainStatus complainStatus) throws NotExistMemberException,
            NotExistPostException, AlreadyComplainException, NotExistCollectionException, UnknownContentTypeException,
            NotExistDMException, NotExistCommentInPostException, NotExistCommentInCollectionException {

        isExistComplain(memberId, complainedId, contentTypeStatus);
        Long id = getComplainedMemberByContentId(complainedId, contentTypeStatus);

        return complainRepository.save(Complain.createComplain(Member.createMember(memberId), Member.createMember(id),
                complainedId, contentTypeStatus, complainStatus)).getId();
    }

    private Long getComplainedMemberByContentId(Long complainedId, ContentTypeStatus contentTypeStatus)
            throws NotExistCollectionException, NotExistPostException, UnknownContentTypeException,
            NotExistDMException, NotExistCommentInCollectionException, NotExistCommentInPostException {
        Long memberId = null;
        if (contentTypeStatus == A) {
            memberId = getPostOwnerId(complainedId);
        } else if (contentTypeStatus == B){
            memberId = getCollectionOwnerId(complainedId);
        } else if (contentTypeStatus == C) {
            memberId = getDMOwnerId(complainedId);
        } else if (contentTypeStatus == D) {
            memberId = getCommentInCollectionOwnerId(complainedId);
        } else if (contentTypeStatus == E) {
            memberId = getCommentInPostOwnerId(complainedId);
        } else if (contentTypeStatus == F) {
            isExistMember(complainedId);
            memberId = complainedId;
        } else {
            throw new UnknownContentTypeException();
        }
        return memberId;
    }

    private Member getPostByIdWithComplainOfMember(Long contentId) throws NotExistPostException {
        Optional<Post> optionalPost = postRepository.findOneByIdWithComplainOfMember(contentId);

        return optionalPost.orElseThrow(NotExistPostException::new).getMember();
    }

    private Member getCollectionByIdWithComplainOfMember(Long contentId) throws NotExistCollectionException {
        Optional<Collection> optionalCollection = collectionRepository.findOneByIdWithComplainOfMember(contentId);

        return optionalCollection.orElseThrow(NotExistCollectionException::new).getMember();
    }

    private Member getCommentInCollectionByIdWithComplainOfMember(Long contentId)
            throws NotExistCommentInCollectionException {

        Optional<CommentInCollection> optionalComment =
                commentInCollectionRepository.findOneByIdWithComplainOfMember(contentId);

        return optionalComment.orElseThrow(NotExistCommentInCollectionException::new).getMember();
    }

    private Member getCommentInPostByIdWithComplainOfMember(Long contentId) throws NotExistCommentInPostException {
        Optional<CommentInPost> optionalComment = commentInPostRepository.findOneByIdWithComplainOfMember(contentId);

        return optionalComment.orElseThrow(NotExistCommentInPostException::new).getMember();
    }

    private Member getDMByIdWithComplainOfMember(Long contentId) throws NotExistDMException {
        Optional<DirectMessage> optionalDM = directMessageRepository.findOneByIdWithComplainOfMember(contentId);

        return optionalDM.orElseThrow(NotExistDMException::new).getMember();
    }

    private Member getMember(Long memberId) throws NotExistMemberException {
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        return optionalMember.orElseThrow(NotExistMemberException::new);
    }

    private void isExistMember(Long memberId) {
        if (!memberQueryRepository.isExist(memberId)) {
            throw new NotExistMemberException();
        }
    }

    private void isExistComplain(Long memberId, Long contentId, ContentTypeStatus contentTypeStatus) {
        if (complainQueryRepository.isExistComplain(memberId, contentId, contentTypeStatus)) {
            throw new AlreadyComplainException();
        }
    }
    private Long getCollectionOwnerId(Long collectionId) {
        Long collectionOwnerId = collectionQueryRepository.getCollectionOwnerId(collectionId);

        if (collectionOwnerId == null) {
            throw new NotExistCollectionException();
        } else {
            return collectionOwnerId;
        }
    }

    private Long getPostOwnerId(Long postId) {
        Long collectionOwnerId = postQueryRepository.getPostOwnerId(postId);

        if (collectionOwnerId == null) {
            throw new NotExistPostException();
        } else {
            return collectionOwnerId;
        }
    }

    private Long getCommentInCollectionOwnerId(Long commentId) {
        Long collectionOwnerId = commentInCollectionQueryRepository.getCommentOwnerId(commentId);

        if (collectionOwnerId == null) {
            throw new NotExistCommentInCollectionException();
        } else {
            return collectionOwnerId;
        }
    }

    private Long getCommentInPostOwnerId(Long commentId) {
        Long collectionOwnerId = commentInPostQueryRepository.getCommentOwnerId(commentId);

        if (collectionOwnerId == null) {
            throw new NotExistCommentInPostException();
        } else {
            return collectionOwnerId;
        }
    }

    private Long getDMOwnerId(Long dmId) {
        Long collectionOwnerId = directMessageQueryRepository.getDMOwnerId(dmId);

        if (collectionOwnerId == null) {
            throw new NotExistDMException();
        } else {
            return collectionOwnerId;
        }
    }
}
