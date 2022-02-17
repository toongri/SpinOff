package com.nameless.spin_off.service.help;

import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.help.ComplainStatus;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.exception.collection.NotExistCollectionException;
import com.nameless.spin_off.exception.member.AlreadyComplainException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.post.NotExistPostException;
import com.nameless.spin_off.repository.collections.CollectionRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaComplainService implements ComplainService{

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CollectionRepository collectionRepository;

    @Override
    public Long insertComplain(Long memberId, Long postId, Long collectionId, ComplainStatus complainStatus) throws NotExistMemberException, NotExistPostException, AlreadyComplainException, NotExistCollectionException {
        Member member = getMember(memberId);

        Post post = getPost(postId);

        Collection collection = getCollection(collectionId);

        Member contentOwnMember = getMemberByPostOrCollection(post, collection);

        return contentOwnMember.addComplain(member, post, collection, complainStatus);
    }

    private Post getPost(Long postId) throws NotExistPostException {
        if (postId == null) {
            return null;
        }
        Optional<Post> optionalPost = postRepository.findOneByIdWithComplainOfMember(postId);
        return optionalPost.orElseThrow(NotExistPostException::new);
    }

    private Collection getCollection(Long collectionId) throws NotExistCollectionException {
        if (collectionId == null) {
            return null;
        }
        Optional<Collection> optionalCollection = collectionRepository.findOneByIdWithComplainOfMember(collectionId);
        return optionalCollection.orElseThrow(NotExistCollectionException::new);
    }

    private Member getMemberByPostOrCollection(Post post, Collection collection) throws NotExistMemberException {
        if (post == null) {
            return collection.getMember();
        } else {
            return post.getMember();
        }
    }

    private Member getMember(Long memberId) throws NotExistMemberException {
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        return optionalMember.orElseThrow(NotExistMemberException::new);
    }
}
