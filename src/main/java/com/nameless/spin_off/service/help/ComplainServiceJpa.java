package com.nameless.spin_off.service.help;

import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.enums.help.ComplainStatus;
import com.nameless.spin_off.enums.help.ContentTypeStatus;
import com.nameless.spin_off.entity.help.Complain;
import com.nameless.spin_off.entity.member.Member;
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
            throw new UnknownContentTypeException(ErrorEnum.UNKNOWN_CONTENT_TYPE);
        }
        return memberId;
    }

    private void isExistMember(Long memberId) {
        if (!memberQueryRepository.isExist(memberId)) {
            throw new NotExistMemberException(ErrorEnum.NOT_EXIST_MEMBER);
        }
    }

    private void isExistComplain(Long memberId, Long contentId, ContentTypeStatus contentTypeStatus) {
        if (complainQueryRepository.isExistComplain(memberId, contentId, contentTypeStatus)) {
            throw new AlreadyComplainException(ErrorEnum.ALREADY_COMPLAIN);
        }
    }
    private Long getCollectionOwnerId(Long collectionId) {
        return collectionQueryRepository.findOwnerIdByCollectionId(collectionId)
                .orElseThrow(() -> new NotExistCollectionException(ErrorEnum.NOT_EXIST_COLLECTION));
    }

    private Long getPostOwnerId(Long postId) {
        return postQueryRepository.findOwnerIdByPostId(postId)
                .orElseThrow(() -> new NotExistPostException(ErrorEnum.NOT_EXIST_POST));
    }

    private Long getCommentInCollectionOwnerId(Long commentId) {
        return commentInCollectionQueryRepository.getCommentOwnerId(commentId)
                .orElseThrow(() -> new NotExistCommentInCollectionException(ErrorEnum.NOT_EXIST_COMMENT_IN_COLLECTION));
    }

    private Long getCommentInPostOwnerId(Long commentId) {
        return commentInPostQueryRepository.findCommentOwnerId(commentId)
                .orElseThrow(() -> new NotExistCommentInPostException(ErrorEnum.NOT_EXIST_COMMENT_IN_POST));
    }

    private Long getDMOwnerId(Long dmId) {
        return directMessageQueryRepository.getDMOwnerId(dmId)
                .orElseThrow(() -> new NotExistDMException(ErrorEnum.NOT_EXIST_DM));
    }
}
