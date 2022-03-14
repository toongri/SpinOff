package com.nameless.spin_off.service.comment;

import com.nameless.spin_off.dto.CommentDto.CreateCommentInCollectionVO;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.comment.CommentInCollection;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.exception.collection.NotExistCollectionException;
import com.nameless.spin_off.exception.comment.AlreadyLikedCommentInCollectionException;
import com.nameless.spin_off.exception.comment.NotExistCommentInCollectionException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.repository.collection.CollectionRepository;
import com.nameless.spin_off.repository.comment.CommentInCollectionRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaCommentInCollectionService implements CommentInCollectionService {

    private final MemberRepository memberRepository;
    private final CollectionRepository collectionRepository;
    private final CommentInCollectionRepository commentInCollectionRepository;

    @Transactional()
    @Override
    public Long insertCommentInCollectionByCommentVO(CreateCommentInCollectionVO commentVO, Long memberId)
            throws NotExistMemberException, NotExistCollectionException, NotExistCommentInCollectionException {

        Member member = getMemberById(memberId);
        Collection collection = getCollectionById(commentVO.getCollectionId());
        CommentInCollection parent = collection.getParentCommentById(commentVO.getParentId());

        CommentInCollection commentInCollection = CommentInCollection
                .createCommentInCollection(member, commentVO.getContent(), parent);

        collection.addCommentInCollection(commentInCollection);

        return commentInCollectionRepository.save(commentInCollection).getId();
    }

    @Transactional()
    @Override
    public Long insertLikedCommentByMemberId(Long memberId, Long commentId)
            throws NotExistMemberException, NotExistCommentInCollectionException, AlreadyLikedCommentInCollectionException {

        Member member = getMemberById(memberId);
        CommentInCollection comment = getCommentByIdWithLikedComment(commentId);

        return comment.insertLikedComment(member);
    }

    private Member getMemberById(Long memberId) throws NotExistMemberException {
        Optional<Member> optionalMember = memberRepository.findById(memberId);

        return optionalMember.orElseThrow(NotExistMemberException::new);
    }

    private CommentInCollection getCommentByIdWithLikedComment(Long commentId) throws NotExistCommentInCollectionException {
        Optional<CommentInCollection> optionalComment =
                commentInCollectionRepository.findOneByIdWithLikedComment(commentId);

        return optionalComment.orElseThrow(NotExistCommentInCollectionException::new);
    }

    private Collection getCollectionById(Long collectionId) throws NotExistCollectionException {
        Optional<Collection> optionalCollection = collectionRepository.findOneByIdWithComment(collectionId);

        return optionalCollection.orElseThrow(NotExistCollectionException::new);
    }
}
