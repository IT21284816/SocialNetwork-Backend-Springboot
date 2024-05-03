package com.linhtch90.psnbackend.controller;

import com.linhtch90.psnbackend.entity.CommentEntity;
import com.linhtch90.psnbackend.entity.CommentPostRequestEntity;
import com.linhtch90.psnbackend.entity.IdObjectEntity;
import com.linhtch90.psnbackend.service.CommentService;
import com.linhtch90.psnbackend.service.ResponseObjectService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping("/insertcomment")
    public ResponseEntity<ResponseObjectService> insertComment(@RequestBody CommentPostRequestEntity postedComment) {
        CommentEntity inputComment = postedComment.getCommentEntity();
        IdObjectEntity inputPostId = postedComment.getPostId();
        return new ResponseEntity<ResponseObjectService>(commentService.insertComment(inputComment, inputPostId.getId()), HttpStatus.OK);
    }

    @PostMapping("/getcomments") 
    public ResponseEntity<ResponseObjectService> getComments(@RequestBody IdObjectEntity inputPostId) {
        return new ResponseEntity<ResponseObjectService>(commentService.getComments(inputPostId.getId()), HttpStatus.OK);
    }
    
    @DeleteMapping("/deletecomment/{commentId}/{postId}")
    public ResponseEntity<ResponseObjectService> deleteComment(
        @PathVariable("commentId") String commentId,
        @PathVariable("postId") String postId
    ) {
        ResponseObjectService response = commentService.deleteComment(commentId, postId);
        HttpStatus status = "success".equals(response.getStatus()) ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(response, status);
    }

    @GetMapping("/comment/{commentId}") // Correct endpoint
    public ResponseEntity<ResponseObjectService> getComment(@PathVariable String commentId) {
        ResponseObjectService response = commentService.getCommentById(commentId);
        HttpStatus status = "success".equals(response.getStatus()) ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(response, status);
    }

    @PutMapping("/updatecomment/{commentId}")
    public ResponseEntity<ResponseObjectService> updateComment(
        @PathVariable("commentId") String commentId,
        @RequestBody CommentEntity updatedComment
    ) {
        ResponseObjectService response = commentService.updateComment(commentId, updatedComment);
        HttpStatus status = "success".equals(response.getStatus()) ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(response, status);
    }
}
