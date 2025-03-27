package application;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
public class UserListTestPage {
    private static final DatabaseHelper databaseHelper = new DatabaseHelper();

    
    public static void main(String[] args) throws SQLException, InterruptedException {
        System.out.println("______________________________________");
        System.out.println("\nTesting Automation: User Management");

        databaseHelper.connectToDatabase();

        // Test getting all users
        List<String> users = databaseHelper.getAllUsers();
        System.out.println("Retrieved Users:");
        for (String user : users) {
            System.out.println(user);
        }
        
        if (!users.isEmpty()) {
            System.out.println("***Success*** Users retrieved successfully.");
        } else {
            System.out.println("***Failure*** No users found in the database.");
        }
    }
}