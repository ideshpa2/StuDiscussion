package hw3TestCases;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import databasePart1.DatabaseHelper;

import application.User;
import application.Question;
import application.Answer;

/**
 * <p><strong>Title:</strong> DeleteAnswerQuestionTestPage</p>
 *
 * <p><strong>Description:</strong> This JUnit test class verifies the full flow of creating and 
 * deleting a {@code Question} and its associated {@code Answer} for a newly registered user.</p>
 *
 * <p>The test ensures that both insertion and deletion operations can be performed
 * without throwing exceptions, confirming data integrity and cleanup logic.</p>
 *
 * <p>Part of HW3 test suite.</p>
 * 
 * @author Ishita Deshpande
 * @version 1.0
 */
public class DeleteAnswerQuestionTestPage {

    /**
     * Shared instance of the {@code DatabaseHelper} class to handle DB operations.
     */
    private static final DatabaseHelper databaseHelper = new DatabaseHelper();

    /**
     * Establishes a connection to the database before executing any test cases.
     *
     * @throws SQLException if the database connection fails
     */
    @BeforeAll
    public static void setup() throws SQLException {
        databaseHelper.connectToDatabase();
        System.out.println("______________________________________");
        System.out.println("\nJUnit Test - Create and Delete Question and Answer");
    }

    /**
     * Performs a complete lifecycle test:
     * <ul>
     *     <li>Creates a test user</li>
     *     <li>Creates a question and an answer</li>
     *     <li>Retrieves the inserted data for confirmation</li>
     *     <li>Deletes the answer and the question</li>
     * </ul>
     *
     * <p>Each step is validated to ensure no exceptions are thrown, indicating that
     * database insertions and deletions are functioning as expected.</p>
     *
     * @throws SQLException if any database operation fails
     */
    @Test
    public void testCreateAndDeleteAnswerAndQuestion() throws SQLException {
        System.out.println("▶ Creating user, question, and answer...");

        // Create a unique test user
        String username = "delete_test_user_" + System.currentTimeMillis();
        User user = new User(0, username, "pass123", username + "@asu.edu", List.of("student"));

        // Register the user
        databaseHelper.register(user, user.getRoles());

        // Retrieve the registered user from the database
        User dbUser = databaseHelper.getUserByUserName(username);

        // Add a new question authored by this user
        Question question = new Question(0, "What is unit testing?", dbUser, "testing");
        databaseHelper.addQuestion(question);

        // Retrieve the inserted question (latest entry)
        List<Question> userQuestions = databaseHelper.getQuestionsByUser(dbUser.getId());
        Question insertedQuestion = userQuestions.get(userQuestions.size() - 1);

        // Add an answer associated with the above question
        Answer answer = new Answer(0, "It helps validate code correctness.", dbUser, insertedQuestion);
        databaseHelper.addAnswer(answer);

        // Retrieve the inserted answer (latest entry)
        List<Answer> answers = databaseHelper.getAnswersByUser(dbUser.getId());
        Answer insertedAnswer = answers.get(answers.size() - 1);

        // Output the created record IDs for debugging
        System.out.println("✅ Created question ID: " + insertedQuestion.getId());
        System.out.println("✅ Created answer ID: " + insertedAnswer.getId());

        // Delete the answer and confirm no exceptions are thrown
        assertDoesNotThrow(() -> databaseHelper.deleteAnswer(insertedAnswer.getId(), dbUser.getId()));
        System.out.println("🗑️ Answer deleted successfully");

        // Delete the question and confirm no exceptions are thrown
        assertDoesNotThrow(() -> databaseHelper.deleteQuestion(insertedQuestion.getId()));
        System.out.println("🗑️ Question deleted successfully");
    }
}

