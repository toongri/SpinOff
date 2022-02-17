package com.nameless.spin_off.service.help;

import com.nameless.spin_off.entity.help.ComplainStatus;
import com.nameless.spin_off.exception.collection.NotExistCollectionException;
import com.nameless.spin_off.exception.member.AlreadyComplainException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.post.NotExistPostException;

public interface ComplainService {

    Long insertComplain(Long memberId, Long postId, Long collectionId, ComplainStatus complainStatus) throws NotExistMemberException, NotExistPostException, AlreadyComplainException, NotExistCollectionException;
}
