package application;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import application.Answer;
import application.Question;
import application.User;
import databasePart1.DatabaseHelper;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AddRemoveTrustedReviewersTest {

	private DatabaseHelper databaseHelper;
	@BeforeAll
    public void setupDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper();
        databaseHelper.connectToDatabase(); // Creates tables
    }

    @BeforeEach
    public void resetState() throws SQLException {
        databaseHelper.connection.createStatement().execute("DELETE FROM trustedreviewers");
    }
    
    
    @Test
    void test() throws SQLException {
        User student = databaseHelper.getUserByUserName("user1");
        User reviewer2 = databaseHelper.getUserByUserName("user2");
        User reviewer3 = databaseHelper.getUserByUserName("user3");

        int studentId = student.getId();
        int reviewer2Id = reviewer2.getId();
        int reviewer3Id = reviewer3.getId();

        // Add two trusted reviewers
        databaseHelper.addTrustedReviewer(studentId, reviewer2Id, 5);
        databaseHelper.addTrustedReviewer(studentId, reviewer3Id, 3);

        // Verify both are added
        Map<User, Integer> reviewers = databaseHelper.getTrustedReviewersForStudent(studentId);
        System.out.println(databaseHelper.getTrustedReviewersForStudent(studentId));
        assertEquals(2, reviewers.size());
        assertTrue(reviewers.containsKey(reviewer2));
        assertTrue(reviewers.containsKey(reviewer3));
        assertEquals(5, reviewers.get(reviewer2));
        assertEquals(3, reviewers.get(reviewer3));

        // Remove one reviewer
        databaseHelper.removeTrustedReviewer(studentId, reviewer2Id);

        // Verify only one remains
        reviewers = databaseHelper.getTrustedReviewersForStudent(studentId);
        assertEquals(1, reviewers.size());
        assertFalse(reviewers.containsKey(reviewer2));
        assertTrue(reviewers.containsKey(reviewer3));
        System.out.println(databaseHelper.getTrustedReviewersForStudent(studentId));
    }

}
