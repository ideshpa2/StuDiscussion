package application;


import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
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
public class StudentToRequestToBeReviewersTEST {
	
    private DatabaseHelper databaseHelper;

    @BeforeAll
    public void setupDatabase() throws SQLException {
        databaseHelper = new DatabaseHelper();
        databaseHelper.connectToDatabase(); // Creates tables
    }

    @BeforeEach
    public void resetState() throws SQLException {
        databaseHelper.connection.createStatement().execute("DELETE FROM studentsRequestReviewerRole");
    }

    @Test
    public void testRequestToBeReviewerAndRequestedReviewer() throws SQLException {
    	databaseHelper.increment_num_users();
    	User user = new User(databaseHelper.get_num_users(),"bond", "Password!123", "john@example.com", Arrays.asList("User", "Student"));
    	databaseHelper.register(user, user.getRoles());
        // Add request
        databaseHelper.requestToBeReviewer(user);

        // Check if request was added
        assertTrue(databaseHelper.requestedReviewer(user), "User should have requested reviewer role.");
    }

 
	@Test
    public void testGetStudentsRequestingReviewerRole() throws SQLException {
		databaseHelper.increment_num_users();
        User user1 = new User(databaseHelper.get_num_users(),"bob", "Password!123", "bob@example.com", Arrays.asList("student"));
        databaseHelper.register(user1, user1.getRoles());
        databaseHelper.increment_num_users();
        User user2 = new User(databaseHelper.get_num_users(),"charlie", "Password!123", "charlie@example.com", Arrays.asList("student"));
        databaseHelper.register(user2, user2.getRoles());

        databaseHelper.requestToBeReviewer(user1);
        databaseHelper.requestToBeReviewer(user2);

        List<String> usernames = databaseHelper.getStudentsRequestingReviewerRole();
        assertEquals(2, usernames.size());
        assertTrue(usernames.contains("bob"));
        assertTrue(usernames.contains("charlie"));
    }

    @Test
    public void testRemoveReviewerRequest() throws SQLException {
    	databaseHelper.increment_num_users();
        User user = new User(databaseHelper.get_num_users(), "dave", "Password!123", "dave@example.com", Arrays.asList("student"));
        databaseHelper.register(user, user.getRoles());

        databaseHelper.requestToBeReviewer(user);
        assertTrue(databaseHelper.requestedReviewer(user));

        String result = databaseHelper.removeReviewerRequest(user);
        assertEquals("", result, "Expected empty string on successful removal.");
        assertFalse(databaseHelper.requestedReviewer(user), "User should no longer have a reviewer request.");
    }

    @Test
    public void testRemoveReviewerRequestForNonexistentUser() throws SQLException {
    	databaseHelper.increment_num_users();
        User fakeUser = new User(databaseHelper.get_num_users(), "nonexistent", "Password!123", "nope@example.com", Arrays.asList("student"));
        databaseHelper.register(fakeUser, fakeUser.getRoles());

        String result = databaseHelper.removeReviewerRequest(fakeUser);
        assertEquals("Failed to delete user.", result);
    }
}
