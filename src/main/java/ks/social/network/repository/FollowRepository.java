package ks.social.network.repository;

import ks.social.network.entity.Follow;
import ks.social.network.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);
    boolean existsByFollowerAndFollowing(User follower, User following);

    @Query("SELECT f.following FROM Follow f WHERE f.follower = :user")
    List<User> findFollowing(@Param("user") User user);

    @Query("SELECT f.follower FROM Follow f WHERE f.following = :user")
    List<User> findFollowers(@Param("user") User user);

    void deleteByFollowerAndFollowing(User follower, User following);
}