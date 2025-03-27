package hw3TestCases;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import databasePart1.DatabaseHelper;

import application.User;
import application.Question;

/**
 * <p><strong>Title:</strong> UpdateAnswerQuestionTestPage</p>
 *
 * <p><strong>Description:</strong> This JUnit test class verifies that a question's content
 * can be successfully updated in the database using the question ID.</p>
 *
 * <p>The test flow includes:
 * <ul>
 *     <li>Registering a new user</li>
 *     <li>Adding a question authored by that user</li>
 *     <li>Updating the content of the question</li>
 *     <li>Retrieving and verifying that the update was applied</li>
 * </ul>
 * </p>
 *
 * <p>This ensures the system's edit functionality works as expected.</p>
 *
 * @author Ishita Deshpande
 * @version 1.0
 */
public class UpdateAnswerQuestionTestPage {

    /** Singleton instance for handling all database operations. */
    private static final DatabaseHelper db = new DatabaseHelper();

    /**
     * Connects to the database once before all test cases are executed.
     *
     * @throws SQLException if the database connection fails
     */
    @BeforeAll
    public static void setup() throws SQLException {
        db.connectToDatabase();
        System.out.println("______________________________________");
        System.out.println("\nJUnit Test - Edit (Update) Question");
    }

    /**
     * Tests whether an existing {@code Question} can be updated with new content.
     *
     * <p>The test performs the following operations:
     * <ul>
     *     <li>Creates a user and inserts a question</li>
     *     <li>Modifies the question text using {@code updateQuestion}</li>
     *     <li>Retrieves the updated record and verifies its content</li>
     * </ul>
     * </p>
     *
     * @throws SQLException if any step of the database operation fails
     */
    @Test
    public void testEditQuestion() throws SQLException {
        System.out.println("▶ Test: Edit Question");

        // Step 1: Create a unique test user and add a question
        String username = "edit_test_user_" + System.currentTimeMillis();
        User user = new User(0, username, "testpass", username + "@asu.edu", List.of("student"));
        db.register(user, user.getRoles());
        User dbUser = db.getUserByUserName(username);

        // Create and insert the question
        Question originalQuestion = new Question(0, "What is Java?", dbUser, "programming");
        db.addQuestion(originalQuestion);

        // Retrieve the inserted question (assume last one added)
        List<Question> questions = db.getQuestionsByUser(dbUser.getId());
        Question insertedQuestion = questions.get(questions.size() - 1);
        System.out.println("✅ Non-edited question: " + insertedQuestion.getContent());

        // Step 2: Update the question content
        String newContent = "What is Java and how does it work?";
        boolean updated = db.updateQuestion(insertedQuestion.getId(), newContent);
        assertTrue(updated, "The question should be successfully updated");

        // Step 3: Fetch the updated question and verify the change
        Question updatedQuestion = db.getQuestionById(insertedQuestion.getId());
        assertEquals(newContent, updatedQuestion.getContent(), "Question content should reflect the updated text");

        System.out.println("✅ Question edited successfully: " + updatedQuestion.getContent());
    }
}


