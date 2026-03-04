package ks.social.network.repository;

import ks.social.network.entity.Like;
import ks.social.network.entity.Post;
import ks.social.network.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserAndPost(User user, Post post);
    boolean existsByUserAndPost(User user, Post post);
    long countByPost(Post post);
    void deleteByUserAndPost(User user, Post post);
}