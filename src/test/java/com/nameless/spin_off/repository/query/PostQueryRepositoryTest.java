package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.exception.post.NotExistPostException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class PostQueryRepositoryTest {

    @Autowired PostQueryRepository postQueryRepository;

    @Test
    public void readPost체크() throws Exception{
        //given

        //when
        assertThatThrownBy(() -> postQueryRepository.findByIdForRead(0L, Collections.emptyList())
                .orElseThrow(() -> new NotExistPostException(ErrorEnum.NOT_EXIST_MEMBER)))
                .isInstanceOf(NotExistPostException.class);
        //then

    }
}