package com.nameless.spin_off.service.help;

import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.comment.CommentInCollection;
import com.nameless.spin_off.entity.comment.CommentInPost;
import com.nameless.spin_off.entity.enums.help.ComplainStatus;
import com.nameless.spin_off.entity.enums.help.ContentTypeStatus;
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
import com.nameless.spin_off.repository.member.DirectMessageRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.nameless.spin_off.entity.enums.help.ContentTypeStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaComplainService implements ComplainService{

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CollectionRepository collectionRepository;
    private final DirectMessageRepository directMessageRepository;
    private final CommentInCollectionRepository commentInCollectionRepository;
    private final CommentInPostRepository commentInPostRepository;

    @Transactional()
    @Override
    public Long insertComplain(Long memberId, Long contentId, ContentTypeStatus contentTypeStatus,
                               ComplainStatus complainStatus) throws NotExistMemberException,
            NotExistPostException, AlreadyComplainException, NotExistCollectionException, UnknownContentTypeException,
            NotExistDMException, NotExistCommentInPostException, NotExistCommentInCollectionException {

        Member member = getMember(memberId);

        Member complainedMember = getComplainedMemberByContentId(contentId, contentTypeStatus);

        return complainedMember.addComplain(member, contentId, contentTypeStatus, complainStatus);
    }

    private Member getComplainedMemberByContentId(Long contentId, ContentTypeStatus contentTypeStatus) throws NotExistCollectionException, NotExistPostException, UnknownContentTypeException, NotExistDMException, NotExistCommentInCollectionException, NotExistCommentInPostException {
        Member complainedMember;
        if (contentTypeStatus == COLLECTION) {
            complainedMember = getCollectionByIdWithComplainOfMember(contentId);
        } else if (contentTypeStatus == POST) {
            complainedMember = getPostByIdWithComplainOfMember(contentId);
        } else if (contentTypeStatus == DM) {
            complainedMember = getDMByIdWithComplainOfMember(contentId);
        } else if (contentTypeStatus == COMMENT_IN_COLLECTION) {
            complainedMember = getCommentInCollectionByIdWithComplainOfMember(contentId);
        } else if (contentTypeStatus == COMMENT_IN_POST) {
            complainedMember = getCommentInPostByIdWithComplainOfMember(contentId);
        } else {
            throw new UnknownContentTypeException();
        }
        return complainedMember;
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
}
