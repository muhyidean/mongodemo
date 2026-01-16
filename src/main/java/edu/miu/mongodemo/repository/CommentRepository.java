package edu.miu.mongodemo.repository;

import edu.miu.mongodemo.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findByContentId(String contentId);
    List<Comment> findByContentType(String contentType);
    List<Comment> findByAuthor(String author);
    List<Comment> findByIsApproved(Boolean isApproved);
    List<Comment> findByContentIdAndContentType(String contentId, String contentType);
}

