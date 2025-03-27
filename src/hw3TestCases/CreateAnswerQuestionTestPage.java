package hw3TestCases;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import databasePart1.DatabaseHelper;

import application.User;
import application.Answer;
import application.Question;

/**
 * <p><strong>Title:</strong> CreateAnswerQuestionTestPage</p>
 *
 * <p><strong>Description:</strong> This JUnit test class verifies that the application correctly
 * allows creation of users, questions, and answers in the system database.</p>
 *
 * <p>The following scenarios are tested:
 * <ul>
 *     <li>Adding a new question for a specific user</li>
 *     <li>Adding an answer to a previously inserted question</li>
 * </ul>
 * </p>
 *
 * <p>Each test ensures the underlying database operations complete successfully without throwing exceptions.</p>
 *
 * @author Ishita Deshpande
 * @version 1.0
 */
public class CreateAnswerQuestionTestPage {

    /**
     * Singleton instance of the database helper to manage DB connections and transactions.
     */
    private static final DatabaseHelper databaseHelper = new DatabaseHelper();

    /**
     * Establishes a database connection once before any test cases are executed.
     *
     * @throws SQLException if a connection to the database cannot be established
     */
    @BeforeAll
    public static void connect() throws SQLException {
        databaseHelper.connectToDatabase();
        System.out.println("______________________________________");
        System.out.println("\nJUnit Test - User/Question/Answer Creation");
    }

    /**
     * Tests whether a question can be successfully added for a given user.
     *
     * <p>The test simulates a student user creating a new question. It does not validate
     * the returned data, only that the action does not throw an exception.</p>
     */
    @Test
    public void testAddQuestion() {
        System.out.println("▶ Add question");

        // Define user role
        List<String> userRole = new ArrayList<>();
        userRole.add("student");

        // Create a user with dummy values
        User user = new User(1, "Jane", "123456", "janeabc@asu.edu", userRole);

        // Create a sample question authored by the user
        Question question = new Question(1, "How are you?", user, "tag1");

        // Assert that the question can be added without any exceptions
        assertDoesNotThrow(() -> databaseHelper.addQuestion(question));

        System.out.println("✅ Question successfully added!");
    }

    /**
     * Tests whether an answer can be successfully added for a given question and user.
     *
     * <p>The test performs a complete flow:
     * <ul>
     *     <li>Registers the user if not already in the system</li>
     *     <li>Adds a question for that user</li>
     *     <li>Retrieves the inserted question from the database</li>
     *     <li>Adds an answer linked to that question</li>
     * </ul>
     * </p>
     *
     * @throws SQLException if any database query fails
     */
    @Test
    public void testAddAnswer() throws SQLException {
        System.out.println("▶ Add answer");

        // Define role and construct user object
        List<String> userRole = List.of("student");
        User user = new User(0, "Jane", "123456", "janeabc@asu.edu", userRole);

        // Register the user if they do not already exist
        if (!databaseHelper.doesUserExist("Jane")) {
            databaseHelper.register(user, userRole);
        }

        // Retrieve the persisted user from the database
        User dbUser = databaseHelper.getUserByUserName("Jane");

        // Create and add a question authored by this user
        Question question = new Question(0, "How are you?", dbUser, "tag1");
        databaseHelper.addQuestion(question);

        // Retrieve the latest question to get its assigned database ID
        List<Question> questions = databaseHelper.getQuestionsByUser(dbUser.getId());
        Question dbQuestion = questions.get(questions.size() - 1); // assume last inserted

        // Create an answer linked to the retrieved question and user
        Answer answer = new Answer(0, "I am good.", dbUser, dbQuestion);

        // Assert that the answer can be added without throwing any exception
        assertDoesNotThrow(() -> databaseHelper.addAnswer(answer));

        System.out.println("✅ Answer successfully added!");
    }
}

