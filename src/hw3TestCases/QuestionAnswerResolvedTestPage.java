package hw3TestCases;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import application.User;
import application.Question;
import databasePart1.DatabaseHelper;

/**
 * <p><strong>Title:</strong> QuestionAnswerResolvedTestPage</p>
 *
 * <p><strong>Description:</strong> This JUnit test class verifies whether questions in the system
 * can be correctly marked as resolved or remain unresolved upon creation.</p>
 *
 * <p>The test cases cover:
 * <ul>
 *     <li>Adding and resolving a question</li>
 *     <li>Adding a question that remains unresolved by default</li>
 * </ul>
 * </p>
 *
 * <p>Part of HW3 test suite focused on resolution status tracking of questions.</p>
 * 
 * @author Ishita Deshpande
 * @version 1.0
 */
public class QuestionAnswerResolvedTestPage {

    /** Shared instance of DatabaseHelper for database interaction. */
    private static final DatabaseHelper db = new DatabaseHelper();

    /**
     * Establishes the database connection before any test method runs.
     *
     * @throws SQLException if database connection fails
     */
    @BeforeAll
    public static void setup() throws SQLException {
        db.connectToDatabase();
    }

    /**
     * Tests the process of adding a question and marking it as resolved.
     *
     * <p>This test:
     * <ul>
     *     <li>Creates a unique test user</li>
     *     <li>Adds a question on behalf of the user</li>
     *     <li>Marks the question as resolved</li>
     *     <li>Validates that the resolution flag is set correctly</li>
     * </ul>
     * </p>
     *
     * @throws SQLException if any database operation fails
     */
    @Test
    public void testAddResolvedQuestion() throws SQLException {
        System.out.println("\n▶ Test: Add Resolved Question");

        // Create a test user with a unique username
        String username = "resolved_" + System.currentTimeMillis();
        User user = new User(0, username, "pass", username + "@asu.edu", List.of("student"));
        db.register(user, user.getRoles());

        // Fetch the registered user from the DB
        User dbUser = db.getUserByUserName(username);
        assertNotNull(dbUser, "User should exist in the database");

        // Create and add a question for the user
        Question question = new Question(0, "What is abstraction?", dbUser, "java");
        db.addQuestion(question);

        // Fetch all questions by the user
        List<Question> questions = db.getQuestionsByUser(dbUser.getId());
        assertFalse(questions.isEmpty(), "Expected at least one question");

        // Mark the first retrieved question as resolved
        Question inserted = questions.get(0);
        db.markQuestionResolved(inserted.getId());

        // Re-fetch the question and check its resolved flag
        Question resolved = db.getQuestionById(inserted.getId());
        assertTrue(resolved.isResolved(), "Question should be marked as resolved");
    }

    /**
     * Tests that a newly added question is unresolved by default.
     *
     * <p>This test:
     * <ul>
     *     <li>Creates a unique test user</li>
     *     <li>Adds a question without modifying its resolution status</li>
     *     <li>Checks that the resolved flag remains false</li>
     * </ul>
     * </p>
     *
     * @throws SQLException if any database operation fails
     */
    @Test
    public void testAddUnresolvedQuestion() throws SQLException {
        System.out.println("\n▶ Test: Add Unresolved Question");

        // Create a second unique test user
        String username = "unresolved_" + System.currentTimeMillis();
        User user = new User(0, username, "pass", username + "@asu.edu", List.of("student"));
        db.register(user, user.getRoles());

        // Fetch the user from the DB
        User dbUser = db.getUserByUserName(username);
        assertNotNull(dbUser, "User should exist in the database");

        // Add a new question, do not mark it resolved
        Question question = new Question(0, "Explain interfaces in Java", dbUser, "java");
        db.addQuestion(question);

        // Fetch questions authored by this user
        List<Question> questions = db.getQuestionsByUser(dbUser.getId());
        assertFalse(questions.isEmpty(), "Expected at least one question");

        // Verify that the question is not resolved by default
        Question inserted = questions.get(0);
        assertFalse(inserted.isResolved(), "Question should be unresolved by default");
    }
}


