package ks.social.network.controller;

import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import ks.social.network.dto.PostDto;
import ks.social.network.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostDto> createPost(
            @RequestParam String content,
            @RequestParam(required = false) String imageUrl) {
        return new ResponseEntity<>(postService.createPost(content, imageUrl), HttpStatus.CREATED);
    }

    @GetMapping("/feed")
    public ResponseEntity<List<PostDto>> getFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.getFeed(page, size));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<PostDto>> getUserPosts(@PathVariable String username) {
        return ResponseEntity.ok(postService.getUserPosts(username));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }
}