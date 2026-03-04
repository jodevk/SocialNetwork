package ks.social.network.service;

import lombok.RequiredArgsConstructor;
import ks.social.network.dto.UserDto;
import ks.social.network.entity.Follow;
import ks.social.network.entity.User;
import ks.social.network.exception.ResourceNotFoundException;
import ks.social.network.repository.FollowRepository;
import ks.social.network.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Transactional
    public void followUser(String username) {
        User currentUser = userService.getCurrentUser();
        User userToFollow = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (currentUser.getId().equals(userToFollow.getId())) {
            throw new RuntimeException("You cannot follow yourself");
        }

        if (!followRepository.existsByFollowerAndFollowing(currentUser, userToFollow)) {
            Follow follow = new Follow(currentUser, userToFollow);
            followRepository.save(follow);
        }
    }

    @Transactional
    public void unfollowUser(String username) {
        User currentUser = userService.getCurrentUser();
        User userToUnfollow = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        followRepository.deleteByFollowerAndFollowing(currentUser, userToUnfollow);
    }

    public List<UserDto> getFollowers(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        User currentUser = userService.getCurrentUser();

        return followRepository.findFollowers(user).stream()
                .map(follower -> convertToUserDto(follower, currentUser))
                .collect(Collectors.toList());
    }

    public List<UserDto> getFollowing(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        User currentUser = userService.getCurrentUser();

        return followRepository.findFollowing(user).stream()
                .map(following -> convertToUserDto(following, currentUser))
                .collect(Collectors.toList());
    }

    private UserDto convertToUserDto(User user, User currentUser) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getFullName());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setBio(user.getBio());
        dto.setFollowedByCurrentUser(
                followRepository.existsByFollowerAndFollowing(currentUser, user)
        );
        return dto;
    }
}
