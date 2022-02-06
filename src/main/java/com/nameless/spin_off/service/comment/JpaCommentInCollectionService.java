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
    public CommentInCollection saveCommentInCollectionByCommentVO(CreateCommentInCollectionVO commentVO) throws NoSuchMemberException, NoSuchCollectionException, NoSuchCommentInCollectionException {

        Member member = getMemberById(commentVO.getMemberId());
        Collection collection = getCollectionById(commentVO.getCollectionId());
        CommentInCollection parent = getCommentInCollectionById(collection, commentVO.getParentId());

        CommentInCollection commentInCollection = CommentInCollection.createCommentInCollection(member, commentVO.getContent(), parent);
        collection.addCommentInCollection(commentInCollection);

        return commentInCollection;
    }

    private CommentInCollection getCommentInCollectionById(Collection collection, Long commentInCollectionId)
            throws NoSuchCommentInCollectionException {

        if (commentInCollectionId == null)
            return null;

        List<CommentInCollection> commentInCollection = collection.getCommentInCollections().stream().filter(comment -> comment.getId().equals(commentInCollectionId))
                .collect(Collectors.toList());

        if (commentInCollection.isEmpty()) {
            throw new NoSuchCommentInCollectionException();
        } else {
            return commentInCollection.get(0);
        }
    }

    private Member getMemberById(Long memberId) throws NoSuchMemberException {
        Optional<Member> optionalMember = memberRepository.findById(memberId);

        return optionalMember.orElseThrow(NoSuchMemberException::new);
    }

    private Collection getCollectionById(Long collectionId) throws NoSuchCollectionException {
        Optional<Collection> optionalCollection = collectionRepository.findOneByIdIncludeCommentInCollection(collectionId);

        return optionalCollection.orElseThrow(NoSuchCollectionException::new);
    }
}
