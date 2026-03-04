package ks.social.network.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private Long id;
    private String content;
    private String imageUrl;
    private LocalDateTime createdAt;
    private UserDto author;
    private long likesCount;
    private boolean likedByCurrentUser;
}