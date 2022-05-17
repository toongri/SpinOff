package com.nameless.spin_off.service.help;

import com.nameless.spin_off.enums.help.ComplainStatus;
import com.nameless.spin_off.enums.help.ContentTypeStatus;
import com.nameless.spin_off.exception.collection.NotExistCollectionException;
import com.nameless.spin_off.exception.comment.NotExistCommentInCollectionException;
import com.nameless.spin_off.exception.comment.NotExistCommentInPostException;
import com.nameless.spin_off.exception.help.UnknownContentTypeException;
import com.nameless.spin_off.exception.member.AlreadyComplainException;
import com.nameless.spin_off.exception.member.NotExistDMException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.post.NotExistPostException;

public interface ComplainService {

    Long insertComplain(Long memberId, Long contentId, ContentTypeStatus contentTypeStatus, ComplainStatus complainStatus)
            throws NotExistMemberException, NotExistPostException, AlreadyComplainException, NotExistCollectionException, UnknownContentTypeException, NotExistDMException, NotExistCommentInPostException, NotExistCommentInCollectionException;
}
