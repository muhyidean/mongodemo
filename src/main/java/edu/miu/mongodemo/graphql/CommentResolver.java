package edu.miu.mongodemo.graphql;

import edu.miu.mongodemo.model.Comment;
import edu.miu.mongodemo.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * GraphQL Resolver for Comment operations
 */
@Controller
public class CommentResolver {

    @Autowired
    private CommentService commentService;

    // ============================================================================
    // QUERIES
    // ============================================================================

    /**
     * Query: Get all comments
     * GraphQL Query:
     * {
     *   comments {
     *     id
     *     author
     *     content
     *     likes
     *   }
     * }
     */
    @QueryMapping
    public List<Comment> comments() {
        return commentService.getAllComments();
    }

    /**
     * Query: Get comments by content ID
     * GraphQL Query:
     * {
     *   commentsByContent(contentId: "123") {
     *     id
     *     author
     *     content
     *     likes
     *   }
     * }
     */
    @QueryMapping
    public List<Comment> commentsByContent(@Argument String contentId) {
        return commentService.getCommentsByContentId(contentId);
    }

    // ============================================================================
    // MUTATIONS
    // ============================================================================

    /**
     * Mutation: Create a new comment
     * GraphQL Mutation:
     * mutation {
     *   createComment(comment: {
     *     contentId: "123"
     *     contentType: ARTICLE
     *     author: "John Doe"
     *     content: "Great article!"
     *   }) {
     *     id
     *     author
     *     content
     *   }
     * }
     */
    @MutationMapping
    public Comment createComment(@Argument("comment") CommentInput input) {
        Comment comment = new Comment(
                input.contentId(),
                input.contentType(),
                input.author(),
                input.content()
        );
        return commentService.createComment(comment);
    }

    /**
     * Mutation: Like a comment (increment likes)
     * GraphQL Mutation:
     * mutation {
     *   likeComment(id: "456") {
     *     id
     *     likes
     *   }
     * }
     */
    @MutationMapping
    public Comment likeComment(@Argument String id) {
        Comment comment = commentService.likeComment(id);
        if (comment == null) {
            throw new RuntimeException("Comment not found with id: " + id);
        }
        return comment;
    }

    // ============================================================================
    // INPUT RECORDS
    // ============================================================================

    /**
     * Input DTO for creating comments
     * Maps to CommentInput in GraphQL schema
     */
    public record CommentInput(
            String contentId,
            String contentType,
            String author,
            String content
    ) {}
}

