package ks.social.network.service;

import lombok.RequiredArgsConstructor;
import ks.social.network.dto.PostDto;
import ks.social.network.dto.UserDto;
import ks.social.network.entity.Post;
import ks.social.network.entity.User;
import ks.social.network.exception.ResourceNotFoundException;
import ks.social.network.repository.LikeRepository;
import ks.social.network.repository.PostRepository;
import ks.social.network.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Transactional
    public PostDto createPost(String content, String imageUrl) {
        User currentUser = userService.getCurrentUser();

        Post post = new Post(content, currentUser);
        if (imageUrl != null) {
            post.setImageUrl(imageUrl);
        }

        Post savedPost = postRepository.save(post);
        return convertToDto(savedPost, currentUser);
    }

    public List<PostDto> getUserPosts(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        User currentUser = userService.getCurrentUser();

        return postRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(post -> convertToDto(post, currentUser))
                .collect(Collectors.toList());
    }

    public List<PostDto> getFeed(int page, int size) {
        User currentUser = userService.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size);

        return postRepository.findFeedForUser(currentUser, pageable).stream()
                .map(post -> convertToDto(post, currentUser))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        User currentUser = userService.getCurrentUser();
        if (!post.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only delete your own posts");
        }

        postRepository.delete(post);
    }

    private PostDto convertToDto(Post post, User currentUser) {
        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setContent(post.getContent());
        dto.setImageUrl(post.getImageUrl());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setLikesCount(likeRepository.countByPost(post));
        dto.setLikedByCurrentUser(
                likeRepository.existsByUserAndPost(currentUser, post)
        );

        // Конвертируем автора
        UserDto authorDto = new UserDto();
        authorDto.setId(post.getUser().getId());
        authorDto.setUsername(post.getUser().getUsername());
        authorDto.setFullName(post.getUser().getFullName());
        authorDto.setAvatarUrl(post.getUser().getAvatarUrl());
        dto.setAuthor(authorDto);

        return dto;
    }
}