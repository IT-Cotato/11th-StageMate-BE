package com.example.stagemate.service.community;

import com.example.stagemate.domain.community.CommunityComment;
import com.example.stagemate.domain.community.CommunityPost;
import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.request.community.CommunityCommentRequest;
import com.example.stagemate.dto.response.community.CommunityCommentResponse;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.repository.community.CommunityCommentRepository;
import com.example.stagemate.repository.community.CommunityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.stagemate.global.exception.community.CommunityErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityCommentService {
    private final CommunityCommentRepository commentRepository;
    private final CommunityRepository communityRepository;

    public List<CommunityCommentResponse> getCommentsByPost(CommunityPost post) {
        List<CommunityComment> rootComments = commentRepository.findRootCommentsWithChildrenByPost(post);
        return rootComments.stream()
                .map(CommunityCommentResponse::new)
                .toList();
    }

    // 커뮤니티 게시글의 댓글 수 증가
    public void addComment(UserJpaEntity user, Long postId, CommunityCommentRequest request) {
        CommunityPost post = communityRepository.findById(postId)
                .orElseThrow(() -> new AppException(COMMUNITY_POST_NOT_FOUND));

        CommunityComment parent = null;
        if (request.parentId() != null) {
            parent = commentRepository.findById(request.parentId())
                    .orElseThrow(() -> new AppException(COMMUNITY_COMMENT_NOT_FOUND));
            if (parent.getParent() != null) {
                throw new AppException(COMMUNITY_REPLY_NOT_ALLOWED);
            }
        }
        CommunityComment comment = request.toEntity(post, user, parent);
        commentRepository.save(comment);
        post.addCommentCount();
    }

    public void updateComment(UserJpaEntity user, Long commentId, String newContent) {
        CommunityComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(COMMUNITY_COMMENT_NOT_FOUND));
        if (comment.getUser() != user) throw new AppException(COMMUNITY_COMMENT_NOT_AUTHOR);
        comment.updateContent(newContent);
    }

    public void deleteComment(UserJpaEntity user, Long commentId) {
        CommunityComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(COMMUNITY_COMMENT_NOT_FOUND));
        if (comment.getUser() != user) throw new AppException(COMMUNITY_COMMENT_NOT_AUTHOR);
        comment.deleteComment();
    }
}
