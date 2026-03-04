package ks.social.network.service;

import lombok.RequiredArgsConstructor;
import ks.social.network.dto.UserDto;
import ks.social.network.entity.User;
import ks.social.network.exception.ResourceNotFoundException;
import ks.social.network.repository.FollowRepository;
import ks.social.network.repository.PostRepository;
import ks.social.network.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final FollowRepository followRepository;
    private final PasswordEncoder passwordEncoder;

    public User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public UserDto getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        User currentUser = getCurrentUser();

        return convertToDto(user, currentUser);
    }

    public List<UserDto> searchUsers(String query) {
        User currentUser = getCurrentUser();
        return userRepository.searchUsers(query).stream()
                .map(user -> convertToDto(user, currentUser))
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDto updateProfile(String username, String bio, String fullName, String avatarUrl) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (bio != null) user.setBio(bio);
        if (fullName != null) user.setFullName(fullName);
        if (avatarUrl != null) user.setAvatarUrl(avatarUrl);

        User savedUser = userRepository.save(user);
        return convertToDto(savedUser, getCurrentUser());
    }

    private UserDto convertToDto(User user, User currentUser) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setBio(user.getBio());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setPostsCount(postRepository.countPostsByUserId(user.getId()));
        dto.setFollowersCount(userRepository.countFollowers(user.getId()));
        dto.setFollowingCount(userRepository.countFollowing(user.getId()));
        dto.setFollowedByCurrentUser(
                followRepository.existsByFollowerAndFollowing(currentUser, user)
        );
        return dto;
    }
}