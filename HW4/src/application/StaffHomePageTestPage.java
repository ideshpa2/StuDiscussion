package application;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import databasePart1.DatabaseHelper;

import java.sql.SQLException;
import java.util.List;

public class StaffHomePageTestPage {

    private static DatabaseHelper dbHelper;
    private static User testUser;

    @BeforeAll
    public static void setup() throws SQLException {
        dbHelper = new DatabaseHelper();
        dbHelper.connectToDatabase();

        testUser = new User(-1, "testStaffUser", "password", "test@email.com", List.of("reviewer"));
        if (!dbHelper.doesUserExist(testUser.getUserName())) {
            dbHelper.register(testUser, testUser.getRoles());
        }
        testUser = dbHelper.getUserByUserName("testStaffUser");
    }

    @Test
    public void testAddToProbationList() throws SQLException {
        dbHelper.addToProbationList(testUser.getId());
        boolean isOnProbation = dbHelper.isReviewerOnProbation(testUser.getId());
        assertTrue(isOnProbation, "User should be added to probation list.");
    }

    @Test
    public void testRemoveFromProbationList() throws SQLException {
        dbHelper.removeFromProbationList(testUser.getId());
        boolean isOnProbation = dbHelper.isReviewerOnProbation(testUser.getId());
        assertFalse(isOnProbation, "User should be removed from probation list.");
    }

    @Test
    public void testRevokeReviewerRole() throws SQLException {
        dbHelper.revokeReviewerRole(testUser.getId());
        User updatedUser = dbHelper.getUserByUserName(testUser.getUserName());
        assertFalse(updatedUser.getRoles().contains("reviewer"), "Reviewer role should be revoked.");
    }

    @Test
    public void testSendMessage() throws SQLException {
        dbHelper.sendMessage(testUser.getId(), testUser.getId(), "Testing staff message functionality.");
        List<String> messages = dbHelper.getMessagesForUser(testUser.getId());
        assertTrue(messages.size() > 0, "Message should be stored and retrievable.");
    }
}
