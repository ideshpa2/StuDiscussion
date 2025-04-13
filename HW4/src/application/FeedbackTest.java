package application;

import databasePart1.DatabaseHelper;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FeedbackTest {

    private DatabaseHelper databaseHelper;

    @BeforeAll
    public void setupDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper();
        databaseHelper.connectToDatabase(); // assumes this sets up schema + test data
    }

    @BeforeEach
    public void resetState() throws SQLException {
        databaseHelper.connection.createStatement().execute("DELETE FROM ReviewFeedback");
    }

    @Test
    public void testAddAndRetrieveFeedback() throws SQLException {
        User student = databaseHelper.getUserByUserName("user1");
        User reviewer = databaseHelper.getUserByUserName("user2");

        // For this to work, ensure answerId and reviewId exist and are valid
        int answerId = 1;  // replace with valid answer ID or insert a mock one if needed
        int reviewId = 1;  // same here
        String feedbackText = "Great review, very helpful!";

        // Add feedback
        databaseHelper.addFeedback(student.getId(), reviewer.getId(), answerId, reviewId, feedbackText);

        // Retrieve feedback
        List<Feedback> feedbackList = databaseHelper.getFeedbackForReviewer(reviewer.getId());

        // Assertions
        assertEquals(1, feedbackList.size());

        Feedback feedback = feedbackList.get(0);
        assertEquals(student.getId(), feedback.getStudent().getId());
        assertEquals(reviewer.getId(), feedback.getReviewerId());
        assertEquals(answerId, feedback.getAnswerId());
        assertEquals(feedbackText, feedback.getContent());
    }
}

