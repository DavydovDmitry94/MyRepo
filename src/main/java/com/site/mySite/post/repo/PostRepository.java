package com.site.mySite.post.repo;

import com.site.mySite.post.models.Post;
import org.springframework.data.repository.CrudRepository;

public interface PostRepository extends CrudRepository<Post, Long> {
}
