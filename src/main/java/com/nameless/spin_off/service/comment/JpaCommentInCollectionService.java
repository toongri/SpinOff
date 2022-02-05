package com.nameless.spin_off.service.comment;

import com.nameless.spin_off.dto.CommentDto;
import com.nameless.spin_off.dto.CommentDto.CreateCommentInCollectionVO;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.comment.CommentInCollection;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.exception.collection.NoSuchCollectionException;
import com.nameless.spin_off.exception.comment.NoSuchCommentInCollectionException;
import com.nameless.spin_off.exception.member.NoSuchMemberException;
import com.nameless.spin_off.repository.collections.CollectionRepository;
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

    @Override
    @Transactional(readOnly = false)
    public CommentInCollection saveCommentInCollectionByCommentVO(CreateCommentInCollectionVO commentVO) throws NoSuchMemberException, NoSuchCollectionException, NoSuchCommentInCollectionException {

        Member member = getMemberById(commentVO.getMemberId());
        Collection collection = getCollectionById(commentVO.getCollectionId());
        CommentInCollection parent = getCommentInCollectionById(commentVO.getParentId());

        CommentInCollection commentInCollection = CommentInCollection.createCommentInCollection(member, commentVO.getContent(), parent);
        collection.addCommentInCollection(commentInCollection);

        return commentInCollection;
    }

    private CommentInCollection getCommentInCollectionById(Long commentInCollectionId) throws NoSuchCommentInCollectionException {
        if (commentInCollectionId == null)
            return null;

        Optional<CommentInCollection> optionalCommentInCollection = commentInCollectionRepository.findById(commentInCollectionId);

        return optionalCommentInCollection.orElseThrow(NoSuchCommentInCollectionException::new);
    }

    private Member getMemberById(Long memberId) throws NoSuchMemberException {
        Optional<Member> optionalMember = memberRepository.findById(memberId);

        return optionalMember.orElseThrow(NoSuchMemberException::new);
    }

    private Collection getCollectionById(Long collectionId) throws NoSuchCollectionException {
        Optional<Collection> optionalCollection = collectionRepository.findById(collectionId);

        return optionalCollection.orElseThrow(NoSuchCollectionException::new);
    }
}
