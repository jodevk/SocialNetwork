package ks.social.network.service;

import lombok.RequiredArgsConstructor;
import ks.social.network.entity.Like;
import ks.social.network.entity.Post;
import ks.social.network.entity.User;
import ks.social.network.exception.ResourceNotFoundException;
import ks.social.network.repository.LikeRepository;
import ks.social.network.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserService userService;

    @Transactional
    public void likePost(Long postId) {
        User currentUser = userService.getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (!likeRepository.existsByUserAndPost(currentUser, post)) {
            Like like = new Like(currentUser, post);
            likeRepository.save(like);
        }
    }

    @Transactional
    public void unlikePost(Long postId) {
        User currentUser = userService.getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        likeRepository.deleteByUserAndPost(currentUser, post);
    }
}