package application;

import java.sql.SQLException;
import java.util.Arrays;
import javafx.application.Application;
import databasePart1.DatabaseHelper;
import javafx.stage.Stage;

/*******
 * <p> Title: PasswordEvaluationTestingAutomation Class. </p>
 * 
 * <p> Description: A Java demonstration for semi-automated tests </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2022 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00	2022-02-25 A set of semi-automated test cases
 * @version 2.00	2024-09-22 Updated for use at ASU
 * 
 */
public class adminDeleteTestPage {
    private static final DatabaseHelper databaseHelper = new DatabaseHelper();

    public static void main(String[] args) throws SQLException {

    	System.out.println("______________________________________");
        System.out.println("\nTesting Automation");

        /************** Start of the test cases **************/
        databaseHelper.increment_num_users();
        User user1 = new User(databaseHelper.get_num_users(),"John", "Password!123", "john@example.com", Arrays.asList("user"));
        
        databaseHelper.increment_num_users();
        User user2 = new User(databaseHelper.get_num_users(),"Jane", "Password!456", "jane@example.com", Arrays.asList("user"));
        
        databaseHelper.connectToDatabase();

        databaseHelper.register(user1, user1.getRoles()); // Pass roles explicitly
        databaseHelper.register(user2, user2.getRoles());

        System.out.println("Users after registration: " + databaseHelper.getAllUsers());

        // Delete users associated with a username (should work)
        databaseHelper.deleteUser("John");
        databaseHelper.deleteUser("Jane");
    
        // Delete user not associated with username (should fail)
        databaseHelper.deleteUser("BEN");
        System.out.println("Users after deletion attempts: " + databaseHelper.getAllUsers());
    }
}