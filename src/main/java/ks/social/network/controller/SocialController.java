package ks.social.network.controller;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ks.social.network.dto.UserDto;
import ks.social.network.service.FollowService;
import ks.social.network.service.LikeService;
import ks.social.network.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/social")
@RequiredArgsConstructor
public class SocialController {
    private final FollowService followService;
    private final LikeService likeService;
    private final UserService userService;

    @PostMapping("/follow/{username}")
    public ResponseEntity<Void> followUser(@PathVariable String username) {
        followService.followUser(username);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/unfollow/{username}")
    public ResponseEntity<Void> unfollowUser(@PathVariable String username) {
        followService.unfollowUser(username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/followers/{username}")
    public ResponseEntity<List<UserDto>> getFollowers(@PathVariable String username) {
        return ResponseEntity.ok(followService.getFollowers(username));
    }

    @GetMapping("/following/{username}")
    public ResponseEntity<List<UserDto>> getFollowing(@PathVariable String username) {
        return ResponseEntity.ok(followService.getFollowing(username));
    }

    @PostMapping("/like/{postId}")
    public ResponseEntity<Void> likePost(@PathVariable Long postId) {
        likeService.likePost(postId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/unlike/{postId}")
    public ResponseEntity<Void> unlikePost(@PathVariable Long postId) {
        likeService.unlikePost(postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/profile/{username}")
    public ResponseEntity<UserDto> getUserProfile(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserProfile(username));
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUsers(@RequestParam String q) {
        return ResponseEntity.ok(userService.searchUsers(q));
    }
}