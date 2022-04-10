package com.nameless.spin_off.service.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentInPostQueryServiceJpa implements CommentInPostQueryService{
}
