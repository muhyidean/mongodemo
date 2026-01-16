package edu.miu.mongodemo.config;

import edu.miu.mongodemo.model.Article;
import edu.miu.mongodemo.model.Comment;
import edu.miu.mongodemo.model.News;
import edu.miu.mongodemo.repository.ArticleRepository;
import edu.miu.mongodemo.repository.CommentRepository;
import edu.miu.mongodemo.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private NewsRepository newsRepository;

    @Override
    public void run(String... args) throws Exception {
        // Clear existing data (optional - remove if you want to keep data)
        // articleRepository.deleteAll();
        // commentRepository.deleteAll();
        // newsRepository.deleteAll();

        // Only insert if database is empty
        if (articleRepository.count() == 0) {
            initializeArticles();
        }
        if (newsRepository.count() == 0) {
            initializeNews();
        }
        if (commentRepository.count() == 0) {
            initializeComments();
        }
    }

    private void initializeArticles() {
        Article article1 = new Article(
            "Introduction to Spring Boot",
            "Spring Boot is a powerful framework that simplifies Java application development. It provides auto-configuration and convention-over-configuration to get you started quickly.",
            "John Doe"
        );
        article1.setTags(Arrays.asList("Spring", "Java", "Framework"));
        article1.setStatus("PUBLISHED");
        article1.setViewCount(150);
        article1.setPublishedDate(LocalDateTime.now().minusDays(5));
        articleRepository.save(article1);

        Article article2 = new Article(
            "MongoDB Best Practices",
            "MongoDB is a NoSQL database that offers flexibility and scalability. Here are some best practices for working with MongoDB in production environments.",
            "Jane Smith"
        );
        article2.setTags(Arrays.asList("MongoDB", "Database", "NoSQL"));
        article2.setStatus("PUBLISHED");
        article2.setViewCount(89);
        article2.setPublishedDate(LocalDateTime.now().minusDays(3));
        articleRepository.save(article2);

        Article article3 = new Article(
            "RESTful API Design Principles",
            "Learn how to design RESTful APIs that are intuitive, scalable, and maintainable. We'll cover HTTP methods, status codes, and API versioning strategies.",
            "Bob Johnson"
        );
        article3.setTags(Arrays.asList("API", "REST", "Web Development"));
        article3.setStatus("DRAFT");
        article3.setViewCount(12);
        articleRepository.save(article3);
    }

    private void initializeNews() {
        News news1 = new News(
            "Breaking: New Technology Breakthrough in AI",
            "Scientists have announced a major breakthrough in artificial intelligence that could revolutionize how we interact with technology.",
            "A team of researchers from leading universities has developed a new AI model that demonstrates unprecedented capabilities in natural language understanding and problem-solving. The technology has shown remarkable results in various applications including healthcare, finance, and education. Experts predict this could be a game-changer for the industry.",
            "Sarah Williams",
            "TECHNOLOGY"
        );
        news1.setPriority(9);
        news1.setImageUrl("https://example.com/images/ai-breakthrough.jpg");
        news1.setPublishedDate(LocalDateTime.now().minusHours(2));
        news1.setExpiryDate(LocalDateTime.now().plusDays(30));
        newsRepository.save(news1);

        News news2 = new News(
            "Local Sports Team Wins Championship",
            "The city's beloved sports team has secured their first championship in over a decade with a thrilling victory in the final match.",
            "In a nail-biting finish, the team overcame a two-point deficit in the final quarter to secure a 98-95 victory. Thousands of fans celebrated in the streets as the team brought home the championship trophy. The victory marks the culmination of a remarkable season.",
            "Mike Anderson",
            "SPORTS"
        );
        news2.setPriority(7);
        news2.setImageUrl("https://example.com/images/championship.jpg");
        news2.setPublishedDate(LocalDateTime.now().minusDays(1));
        news2.setExpiryDate(LocalDateTime.now().plusDays(60));
        newsRepository.save(news2);

        News news3 = new News(
            "Economic Growth Exceeds Expectations",
            "The latest economic indicators show stronger-than-expected growth, signaling a robust recovery from recent market fluctuations.",
            "Financial analysts are optimistic about the future as key economic metrics continue to improve. The manufacturing sector showed particular strength, with increased production and employment numbers. Government officials praised the resilience of the economy.",
            "Emily Chen",
            "BUSINESS"
        );
        news3.setPriority(6);
        news3.setImageUrl("https://example.com/images/economy.jpg");
        news3.setPublishedDate(LocalDateTime.now().minusHours(5));
        news3.setExpiryDate(LocalDateTime.now().plusDays(45));
        newsRepository.save(news3);

        News news4 = new News(
            "New Healthcare Policy Announced",
            "Government officials have unveiled a comprehensive new healthcare policy aimed at improving access and affordability.",
            "The new policy includes provisions for expanded coverage, reduced prescription costs, and improved mental health services. Healthcare advocates have expressed support for the initiative, which is expected to benefit millions of citizens.",
            "Robert Martinez",
            "POLITICS"
        );
        news4.setPriority(8);
        news4.setPublishedDate(LocalDateTime.now().minusDays(2));
        news4.setExpiryDate(LocalDateTime.now().plusDays(90));
        newsRepository.save(news4);
    }

    private void initializeComments() {
        // Get saved articles and news to reference
        Article article1 = articleRepository.findAll().stream()
            .filter(a -> a.getTitle().contains("Spring Boot"))
            .findFirst().orElse(null);
        
        Article article2 = articleRepository.findAll().stream()
            .filter(a -> a.getTitle().contains("MongoDB"))
            .findFirst().orElse(null);

        News news1 = newsRepository.findAll().stream()
            .filter(n -> n.getHeadline().contains("AI"))
            .findFirst().orElse(null);

        if (article1 != null) {
            Comment comment1 = new Comment(
                article1.getId(),
                "ARTICLE",
                "Alice Brown",
                "Great article! Very helpful for beginners. Could you expand on the dependency injection section?"
            );
            comment1.setIsApproved(true);
            comment1.setLikes(5);
            comment1.setCreatedAt(LocalDateTime.now().minusDays(4));
            commentRepository.save(comment1);

            Comment comment2 = new Comment(
                article1.getId(),
                "ARTICLE",
                "Charlie Davis",
                "Thanks for sharing. I've been using Spring Boot for a while, and this covers the basics well."
            );
            comment2.setIsApproved(true);
            comment2.setLikes(2);
            comment2.setCreatedAt(LocalDateTime.now().minusDays(3));
            commentRepository.save(comment2);
        }

        if (article2 != null) {
            Comment comment3 = new Comment(
                article2.getId(),
                "ARTICLE",
                "Diana Foster",
                "Excellent insights on MongoDB best practices. The indexing strategies section was particularly useful."
            );
            comment3.setIsApproved(true);
            comment3.setLikes(7);
            comment3.setCreatedAt(LocalDateTime.now().minusDays(2));
            commentRepository.save(comment3);
        }

        if (news1 != null) {
            Comment comment4 = new Comment(
                news1.getId(),
                "NEWS",
                "George Harris",
                "This is incredible! I can't wait to see how this technology develops. What are the potential ethical concerns?"
            );
            comment4.setIsApproved(true);
            comment4.setLikes(12);
            comment4.setCreatedAt(LocalDateTime.now().minusHours(1));
            commentRepository.save(comment4);

            Comment comment5 = new Comment(
                news1.getId(),
                "NEWS",
                "Hannah Lee",
                "Amazing breakthrough! This could have huge implications for various industries."
            );
            comment5.setIsApproved(true);
            comment5.setLikes(8);
            comment5.setCreatedAt(LocalDateTime.now().minusMinutes(30));
            commentRepository.save(comment5);

            Comment comment6 = new Comment(
                news1.getId(),
                "NEWS",
                "Ian Murphy",
                "I'm skeptical about these claims. We need to see more peer-reviewed research."
            );
            comment6.setIsApproved(false); // Pending approval
            comment6.setLikes(1);
            comment6.setCreatedAt(LocalDateTime.now().minusMinutes(15));
            commentRepository.save(comment6);
        }
    }
}

