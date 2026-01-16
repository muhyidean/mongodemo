package edu.miu.mongodemo.controller;

import edu.miu.mongodemo.model.Comment;
import edu.miu.mongodemo.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    
    @Autowired
    private CommentService commentService;

    @GetMapping
    public ResponseEntity<List<Comment>> getAllComments() {
        return ResponseEntity.ok(commentService.getAllComments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable String id) {
        Optional<Comment> comment = commentService.getCommentById(id);
        return comment.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Comment> createComment(@RequestBody Comment comment) {
        Comment createdComment = commentService.createComment(comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comment> updateComment(@PathVariable String id, @RequestBody Comment comment) {
        Comment updatedComment = commentService.updateComment(id, comment);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable String id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/content/{contentId}")
    public ResponseEntity<List<Comment>> getCommentsByContentId(@PathVariable String contentId) {
        return ResponseEntity.ok(commentService.getCommentsByContentId(contentId));
    }

    @GetMapping("/type/{contentType}")
    public ResponseEntity<List<Comment>> getCommentsByContentType(@PathVariable String contentType) {
        return ResponseEntity.ok(commentService.getCommentsByContentType(contentType));
    }

    @GetMapping("/author/{author}")
    public ResponseEntity<List<Comment>> getCommentsByAuthor(@PathVariable String author) {
        return ResponseEntity.ok(commentService.getCommentsByAuthor(author));
    }

    @GetMapping("/approved/{isApproved}")
    public ResponseEntity<List<Comment>> getApprovedComments(@PathVariable Boolean isApproved) {
        return ResponseEntity.ok(commentService.getApprovedComments(isApproved));
    }

    @GetMapping("/content/{contentId}/type/{contentType}")
    public ResponseEntity<List<Comment>> getCommentsByContent(
            @PathVariable String contentId, 
            @PathVariable String contentType) {
        return ResponseEntity.ok(commentService.getCommentsByContent(contentId, contentType));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Comment> likeComment(@PathVariable String id) {
        Comment comment = commentService.likeComment(id);
        if (comment != null) {
            return ResponseEntity.ok(comment);
        }
        return ResponseEntity.notFound().build();
    }
}

