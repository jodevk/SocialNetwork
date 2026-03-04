package ks.social.network.repository;

import ks.social.network.entity.Post;
import ks.social.network.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserOrderByCreatedAtDesc(User user);

    @Query("SELECT p FROM Post p WHERE p.user IN " +
            "(SELECT f.following FROM Follow f WHERE f.follower = :user) " +
            "ORDER BY p.createdAt DESC")
    List<Post> findFeedForUser(@Param("user") User user, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.user.id = :userId")
    long countPostsByUserId(@Param("userId") Long userId);
}