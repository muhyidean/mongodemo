package edu.miu.mongodemo.service;

import edu.miu.mongodemo.model.Comment;
import edu.miu.mongodemo.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    
    @Autowired
    private CommentRepository commentRepository;

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    public Optional<Comment> getCommentById(String id) {
        return commentRepository.findById(id);
    }

    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    public Comment updateComment(String id, Comment comment) {
        comment.setId(id);
        return commentRepository.save(comment);
    }

    public void deleteComment(String id) {
        commentRepository.deleteById(id);
    }

    public List<Comment> getCommentsByContentId(String contentId) {
        return commentRepository.findByContentId(contentId);
    }

    public List<Comment> getCommentsByContentType(String contentType) {
        return commentRepository.findByContentType(contentType);
    }

    public List<Comment> getCommentsByAuthor(String author) {
        return commentRepository.findByAuthor(author);
    }

    public List<Comment> getApprovedComments(Boolean isApproved) {
        return commentRepository.findByIsApproved(isApproved);
    }

    public List<Comment> getCommentsByContent(String contentId, String contentType) {
        return commentRepository.findByContentIdAndContentType(contentId, contentType);
    }

    public Comment likeComment(String id) {
        Optional<Comment> commentOpt = commentRepository.findById(id);
        if (commentOpt.isPresent()) {
            Comment comment = commentOpt.get();
            comment.setLikes(comment.getLikes() + 1);
            return commentRepository.save(comment);
        }
        return null;
    }
}

