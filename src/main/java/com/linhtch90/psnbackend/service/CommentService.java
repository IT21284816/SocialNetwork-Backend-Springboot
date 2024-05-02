package com.linhtch90.psnbackend.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.linhtch90.psnbackend.entity.CommentEntity;
import com.linhtch90.psnbackend.entity.IdObjectEntity;
import com.linhtch90.psnbackend.entity.PostEntity;
import com.linhtch90.psnbackend.repository.CommentRepository;
import com.linhtch90.psnbackend.repository.PostRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepo;

    @Autowired
    private PostRepository postRepo;

    @Autowired
    private PostService postService;

    public ResponseObjectService insertComment(CommentEntity inputComment, String inputPostId) {
        ResponseObjectService responseObj = new ResponseObjectService();
        Optional<PostEntity> optPost = postRepo.findById(inputPostId);
        if (optPost.isEmpty()) {
            responseObj.setStatus("fail");
            responseObj.setMessage("cannot find target post id: " + inputPostId);
            responseObj.setPayload(null);
            return responseObj;
        } else {
            inputComment.setCreatedAt(Instant.now());
            commentRepo.save(inputComment);
            PostEntity targetPost = optPost.get();
            List<CommentEntity> commentList = targetPost.getComment();
            if (commentList == null) {
                commentList = new ArrayList<>();
            }
            commentList.add(inputComment);
            targetPost.setComment(commentList);
            postService.updatePostByComment(targetPost);
            responseObj.setStatus("success");
            responseObj.setMessage("success");
            responseObj.setPayload(inputComment);
            return responseObj;
        }
    }

    public ResponseObjectService getComments(String inputPostId) {
        ResponseObjectService responseObj = new ResponseObjectService();
        Optional<PostEntity> optTargetPost = postRepo.findById(inputPostId);
        if (optTargetPost.isEmpty()) {
            responseObj.setStatus("fail");
            responseObj.setMessage("fail");
            responseObj.setPayload(null);
            return responseObj;
        } else {
            PostEntity targetPost = optTargetPost.get();
            List<CommentEntity> commentList = targetPost.getComment();
            if (commentList.size() > 0) {
                responseObj.setStatus("success");
                responseObj.setMessage("success");
                responseObj.setPayload(commentList);
                return responseObj;
            } else {
                responseObj.setStatus("success");
                responseObj.setMessage("Post id " + inputPostId + " does not have any comment");
                responseObj.setPayload(null);
                return responseObj;
            }
        }
    }

    public ResponseObjectService deleteComment(String commentId, String postId) {
        ResponseObjectService responseObj = new ResponseObjectService();
        
        // Check if the comment exists
        Optional<CommentEntity> optComment = commentRepo.findById(commentId);
        if (optComment.isEmpty()) {
            responseObj.setStatus("fail");
            responseObj.setMessage("Comment not found: " + commentId);
            responseObj.setPayload(null);
            return responseObj;
        }
        
        // Check if the post exists
        Optional<PostEntity> optPost = postRepo.findById(postId);
        if (optPost.isEmpty()) {
            responseObj.setStatus("fail");
            responseObj.setMessage("Post not found: " + postId);
            responseObj.setPayload(null);
            return responseObj;
        }
        
        // Remove the comment from the post
        PostEntity targetPost = optPost.get();
        List<CommentEntity> commentList = targetPost.getComment();
        commentList = commentList.stream()
            .filter(comment -> !comment.getId().equals(commentId))
            .collect(Collectors.toList());

        targetPost.setComment(commentList);
        
        // Save the post and delete the comment
        postService.updatePostByComment(targetPost);
        commentRepo.deleteById(commentId);
        
        responseObj.setStatus("success");
        responseObj.setMessage("Comment deleted successfully");
        responseObj.setPayload(null);

        return responseObj;
    }

}
