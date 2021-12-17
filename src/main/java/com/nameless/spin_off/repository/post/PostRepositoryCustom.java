package com.nameless.spin_off.repository.post;


import com.nameless.spin_off.domain.post.Post;

import java.util.List;

public interface PostRepositoryCustom {
    public void save(Post post);
    public Post findOne(Long id);
    public List<Post> findAll();
    public List<Post> findByTitle(String title);
}
