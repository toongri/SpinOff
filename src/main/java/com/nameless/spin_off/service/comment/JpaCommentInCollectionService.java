package com.nameless.spin_off.service.comment;

import com.nameless.spin_off.dto.CommentDto.CreateCommentInCollectionVO;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.comment.CommentInCollection;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.exception.collection.NotSearchCollectionException;
import com.nameless.spin_off.exception.comment.NotSearchCommentInCollectionException;
import com.nameless.spin_off.exception.member.NotSearchMemberException;
import com.nameless.spin_off.repository.collections.CollectionRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaCommentInCollectionService implements CommentInCollectionService {

    private final MemberRepository memberRepository;
    private final CollectionRepository collectionRepository;

    @Override
    @Transactional(readOnly = false)
    public CommentInCollection insertCommentInCollectionByCommentVO(CreateCommentInCollectionVO commentVO)
            throws NotSearchMemberException, NotSearchCollectionException, NotSearchCommentInCollectionException {

        Member member = getMemberById(commentVO.getMemberId());
        Collection collection = getCollectionById(commentVO.getCollectionId());
        CommentInCollection parent = collection.getParentCommentById(commentVO.getParentId());

        CommentInCollection commentInCollection = CommentInCollection
                .createCommentInCollection(member, commentVO.getContent(), parent);
        collection.addCommentInCollection(commentInCollection);

        return commentInCollection;
    }

    private Member getMemberById(Long memberId) throws NotSearchMemberException {
        Optional<Member> optionalMember = memberRepository.findById(memberId);

        return optionalMember.orElseThrow(NotSearchMemberException::new);
    }

    private Collection getCollectionById(Long collectionId) throws NotSearchCollectionException {
        Optional<Collection> optionalCollection = collectionRepository.findOneByIdIncludeCommentInCollection(collectionId);

        return optionalCollection.orElseThrow(NotSearchCollectionException::new);
    }
}
