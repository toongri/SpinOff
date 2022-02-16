package com.nameless.spin_off.service.hashtag;

import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.exception.hashtag.NotExistHashtagException;
import com.nameless.spin_off.repository.hashtag.HashtagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaHashtagService implements HashtagService{

    private final HashtagRepository hashtagRepository;

    @Override
    public Long insertViewedHashtagByIp(String ip, Long hashtagId) throws NotExistHashtagException {

        Hashtag hashtag = getHashtagByIdWithViewedByIp(hashtagId);

        return null;
    }

    public Hashtag getHashtagByIdWithViewedByIp(Long hashtagId) throws NotExistHashtagException {
        Optional<Hashtag> optionalHashtag = hashtagRepository.findOneByIdWithViewedByIp(hashtagId);

        return optionalHashtag.orElseThrow(NotExistHashtagException::new);
    }
}
