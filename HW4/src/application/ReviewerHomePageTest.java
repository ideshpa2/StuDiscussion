package application;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Arrays;

import databasePart1.DatabaseHelper;
import application.*;
import org.junit.jupiter.api.*;

public class ReviewerHomePageTest {

    private static DatabaseHelper db;
    private static User reviewer;
    private static User student;
    private static Answer dummyAnswer;
    private static Question dummyQuestion;

    @BeforeAll
    static void setupAll() throws SQLException {
        db = new DatabaseHelper();
        db.connectToDatabase();

        if (!db.doesUserExist("testReviewer")) {
            reviewer = new User(0, "testReviewer", "pass", "test@example.com", Arrays.asList("reviewer"));
            db.register(reviewer, reviewer.getRoles());
        }
        reviewer = db.getUserByUserName("testReviewer");

        if (!db.doesUserExist("testStudent")) {
            student = new User(0, "testStudent", "pass", "student@example.com", Arrays.asList("student"));
            db.register(student, student.getRoles());
        }
        student = db.getUserByUserName("testStudent");

        List<Question> studentQuestions = db.getQuestionsByUser(student.getId());
        dummyQuestion = studentQuestions.stream()
                .filter(q -> q.getContent().equals("What is Java?")).findFirst().orElse(null);

        if (dummyQuestion == null) {
            Question q = new Question(0, "What is Java?", student, "General");
            db.addQuestion(q);
            dummyQuestion = db.getQuestionsByUser(student.getId()).stream()
                    .filter(qq -> qq.getContent().equals("What is Java?")).findFirst().orElse(null);
        }

        dummyAnswer = new Answer(0, "Java is a programming language.", student, dummyQuestion);
        db.addAnswer(dummyAnswer);

        // Retrieve the persisted answer with generated ID
        List<Answer> answers = db.getAnswersByQuestionId(dummyQuestion.getId());
        dummyAnswer = answers.stream()
                .filter(a -> a.getContent().equals("Java is a programming language.")).findFirst()
                .orElseThrow(() -> new IllegalStateException("Failed to retrieve dummy answer"));
    }

    @Test
    void testCreateReview() throws SQLException {
        Review review = new Review(0, "Good answer.", reviewer, dummyAnswer, null);
        db.addReview(review);

        List<Review> reviews = db.getReviewsByReviewerId(reviewer.getId());
        assertTrue(reviews.stream().anyMatch(r -> r.getContent().equals("Good answer.")));
    }

    @Test
    void testUpdateReviewCreatesHistory() throws SQLException {
        Review original = new Review(0, "Initial feedback.", reviewer, dummyAnswer, null);
        db.addReview(original);

        List<Review> all = db.getReviewsByReviewerId(reviewer.getId());
        Review toUpdate = all.stream().filter(r -> r.getContent().equals("Initial feedback.")).findFirst().orElse(null);
        assertNotNull(toUpdate);

        toUpdate.setContent("Updated feedback.");
        db.addReview(new Review(0, toUpdate.getContent(), reviewer, dummyAnswer, toUpdate));

        List<Review> updated = db.getReviewsByReviewerId(reviewer.getId());
        assertTrue(updated.stream().anyMatch(r -> "Updated feedback.".equals(r.getContent())));
        assertTrue(updated.stream().anyMatch(r -> "Initial feedback.".equals(r.getContent())));
    }

    @Test
    void testDeleteReview() throws SQLException {
        Review review = new Review(0, "To be deleted", reviewer, dummyAnswer, null);
        db.addReview(review);

        List<Review> all = db.getReviewsByReviewerId(reviewer.getId());
        Review toDelete = all.stream().filter(r -> r.getContent().equals("To be deleted")).findFirst().orElse(null);
        assertNotNull(toDelete);

        db.deleteReview(toDelete.getId());
        List<Review> afterDelete = db.getReviewsByReviewerId(reviewer.getId());
        assertFalse(afterDelete.stream().anyMatch(r -> r.getContent().equals("To be deleted")));
    }

    @Test
    void testFetchUserReviews() throws SQLException {
        List<Review> reviews = db.getReviewsByReviewerId(reviewer.getId());
        assertNotNull(reviews);
        assertFalse(reviews.isEmpty());
    }

    @Test
    void testViewOriginalReview() throws SQLException {
        Review base = new Review(0, "Original base review", reviewer, dummyAnswer, null);
        db.addReview(base);

        Review fetched = db.getReviewsByReviewerId(reviewer.getId()).stream()
                .filter(r -> "Original base review".equals(r.getContent())).findFirst().orElse(null);
        assertNotNull(fetched);

        Review update = new Review(0, "Modified version", reviewer, dummyAnswer, fetched);
        db.addReview(update);

        Review confirm = db.getReviewsByReviewerId(reviewer.getId()).stream()
                .filter(r -> "Modified version".equals(r.getContent())).findFirst().orElse(null);
        assertNotNull(confirm);
        assertEquals(fetched.getId(), confirm.getOriginalReview().getId());
    }

    @Test
    void testSwitchToReviewerRole() {
        List<String> roles = db.getUserRoles(reviewer.getUserName());
        assertTrue(roles.contains("reviewer"));
    }

    @Test
    void testDisplayAllReviewsForAnswer() throws SQLException {
        List<Review> reviews = db.getReviewsByAnswerId(dummyAnswer.getId());
        assertNotNull(reviews);
    }
}

