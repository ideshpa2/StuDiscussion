package hw3TestCases;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import databasePart1.DatabaseHelper;

import application.User;
import application.Question;
import application.Answer;

/**
 * <p><strong>Title:</strong> ReadAnswerQuestionTestPage</p>
 *
 * <p><strong>Description:</strong> This JUnit test class verifies that the application correctly
 * supports retrieving {@code Question} and {@code Answer} records from the database by their IDs.</p>
 *
 * <p>Test scenarios include:
 * <ul>
 *     <li>Creating and retrieving an answer using its unique ID</li>
 *     <li>Creating and retrieving a question using its unique ID</li>
 * </ul>
 * </p>
 *
 * <p>These tests confirm the integrity of read operations after data insertion.</p>
 * 
 * @author Ishita Deshpande
 * @version 1.0
 */
public class ReadAnswerQuestionTestPage {

    /** Shared instance for interacting with the database. */
    private static final DatabaseHelper db = new DatabaseHelper();

    /**
     * Establishes a connection to the database before any test is executed.
     *
     * @throws SQLException if a database connection cannot be established
     */
    @BeforeAll
    public static void setup() throws SQLException {
        db.connectToDatabase();
        System.out.println("______________________________________");
        System.out.println("\nJUnit Test - Read Question and Answer by ID");
    }

    /**
     * Tests whether an {@code Answer} can be correctly retrieved by its unique database ID.
     *
     * <p>This test performs the following steps:
     * <ul>
     *     <li>Creates a user and adds a question</li>
     *     <li>Adds an answer linked to that question</li>
     *     <li>Retrieves the answer using its ID</li>
     *     <li>Verifies that the fetched answer is not null and content matches</li>
     * </ul>
     * </p>
     *
     * @throws SQLException if any database operation fails
     */
    @Test
    public void testGetAnswerById() throws SQLException {
        System.out.println("▶ Insert and Read Answer by ID");

        // Step 1: Create a unique user
        String username = "read_user_" + System.currentTimeMillis();
        User user = new User(0, username, "pass123", username + "@asu.edu", List.of("student"));
        db.register(user, user.getRoles());
        User dbUser = db.getUserByUserName(username);

        // Step 2: Add a question for the user
        Question question = new Question(0, "What is JUnit?", dbUser, "testing");
        db.addQuestion(question);

        // Retrieve the inserted question from the DB
        Question dbQuestion = db.getQuestionsByUser(dbUser.getId()).get(0);

        // Step 3: Add an answer to the retrieved question
        Answer answer = new Answer(0, "JUnit is a testing framework.", dbUser, dbQuestion);
        db.addAnswer(answer);

        // Retrieve the inserted answer
        Answer dbAnswer = db.getAnswersByUser(dbUser.getId()).get(0);

        // Step 4: Fetch the answer by ID and validate
        Answer fetchedAnswer = db.getAnswerById(dbAnswer.getId());
        assertNotNull(fetchedAnswer, "Expected to retrieve an answer by ID");
        assertEquals("JUnit is a testing framework.", fetchedAnswer.getContent());

        System.out.println("✅ Answer read: " + fetchedAnswer.getContent());
    }

    /**
     * Tests whether a {@code Question} can be correctly retrieved by its unique database ID.
     *
     * <p>This test:
     * <ul>
     *     <li>Registers a user and adds a new question</li>
     *     <li>Retrieves the question using its assigned ID</li>
     *     <li>Verifies that the retrieved question content matches the original</li>
     * </ul>
     * </p>
     *
     * @throws SQLException if any database operation fails
     */
    @Test
    public void testGetQuestionById() throws SQLException {
        System.out.println("▶ Insert and Read Question by ID");

        // Step 1: Create a user
        String username = "read_question_user_" + System.currentTimeMillis();
        User user = new User(0, username, "pass123", username + "@asu.edu", List.of("student"));
        db.register(user, user.getRoles());
        User dbUser = db.getUserByUserName(username);

        // Step 2: Add a question
        Question question = new Question(0, "What is SQL?", dbUser, "database");
        db.addQuestion(question);

        // Retrieve the inserted question
        Question dbQuestion = db.getQuestionsByUser(dbUser.getId()).get(0);

        // Step 3: Fetch the question by ID and validate
        Question fetched = db.getQuestionById(dbQuestion.getId());
        assertNotNull(fetched, "Expected to retrieve a question by ID");
        assertEquals("What is SQL?", fetched.getContent());

        System.out.println("✅ Question read: " + fetched.getContent());
    }
}


