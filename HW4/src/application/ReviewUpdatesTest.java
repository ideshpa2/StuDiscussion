package application;

import databasePart1.DatabaseHelper;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReviewUpdatesTest {

    private DatabaseHelper databaseHelper;

    @BeforeAll
    public void setup() throws SQLException {
        databaseHelper = new DatabaseHelper();
        databaseHelper.connectToDatabase();
    }

    @BeforeEach
    public void resetState() throws SQLException {
        databaseHelper.connection.createStatement().execute("DELETE FROM ReviewUpdates");
        databaseHelper.connection.createStatement().execute("DELETE FROM Reviews");
    }

    @Test
    public void testStudentCanViewAndMarkReviewUpdate() throws SQLException {
        // Setup users
        User student = databaseHelper.getUserByUserName("user1");
        User reviewer = databaseHelper.getUserByUserName("user2");

        int studentId = student.getId();
        int reviewerId = reviewer.getId();

        // Step 1: Insert a mock review
        String reviewContent = "This is a new review from trusted reviewer";
        int reviewId;

        int answerId;
        try (PreparedStatement stmt = databaseHelper.connection.prepareStatement(
                "INSERT INTO Answers (question_id, user_id, content) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, 1); // Use a valid question_id, adjust as needed
            stmt.setInt(2, reviewerId); // The one who answered
            stmt.setString(3, "Sample answer content");
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            assertTrue(rs.next());
            answerId = rs.getInt(1);
        }
        try (PreparedStatement stmt = databaseHelper.connection.prepareStatement(
                "INSERT INTO Reviews (reviewer_id, answer_id, content) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, reviewerId);
            stmt.setInt(2, answerId);
            stmt.setString(3, reviewContent);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            assertTrue(rs.next());
            reviewId = rs.getInt(1);
        }
        // Step 2: Insert a review update (unviewed)
        int updateId;
        try (PreparedStatement stmt = databaseHelper.connection.prepareStatement(
                "INSERT INTO ReviewUpdates (student_id, reviewer_id, review_id, viewed, timestamp) " +
                "VALUES (?, ?, ?, FALSE, CURRENT_TIMESTAMP)", Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, reviewerId);
            stmt.setInt(3, reviewId);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            assertTrue(rs.next());
            updateId = rs.getInt(1);
        }

        // Step 3: Fetch unviewed updates — should contain the new update
        List<ReviewUpdate> updates = databaseHelper.getUnviewedReviewUpdates(studentId);
        assertEquals(1, updates.size());
        ReviewUpdate update = updates.get(0);
        assertEquals(reviewContent, update.getReviewText());

        // Step 4: Mark the update as viewed
        databaseHelper.markReviewUpdateAsViewed(updateId);

        // Step 5: Verify that no updates are shown now
        List<ReviewUpdate> afterMarking = databaseHelper.getUnviewedReviewUpdates(studentId);
        assertEquals(0, afterMarking.size());
    }
}
