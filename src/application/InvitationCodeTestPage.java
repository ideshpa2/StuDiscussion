package application;

import java.sql.SQLException;
import java.util.Arrays;
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
public class InvitationCodeTestPage {
    private static final DatabaseHelper databaseHelper = new DatabaseHelper();

    /*
     * This mainline displays a header to the console, performs a sequence of
     * test cases, and then displays a footer with a summary of the results
     */
    public static void main(String[] args) throws SQLException, InterruptedException {
        /************** Test cases semi-automation report header **************/
        System.out.println("______________________________________");
        System.out.println("\nTesting Automation: Invitation Code Management");

        databaseHelper.connectToDatabase();

        // Test generating an invitation code
        String invitationCode = databaseHelper.generateInvitationCode();
        System.out.println("Generated Invitation Code: " + invitationCode);
        
        // Validate the invitation code is generated and stored
        boolean isValid = databaseHelper.validateInvitationCode(invitationCode);
        if (isValid) {
            System.out.println("***Success*** Invitation code is valid and marked as used.");
        } else {
            System.out.println("***Failure*** Invitation code validation failed.");
        }
        
        System.out.println("Waiting for expiration period...");
        TimeUnit.SECONDS.sleep(5); // Simulate delay (adjust as per actual expiration logic)
        
        // Attempt to reuse the invitation code (should fail as it is already used)
        boolean isStillValid = databaseHelper.validateInvitationCode(invitationCode);
        if (!isStillValid) {
            System.out.println("***Success*** Expired or used invitation code was rejected.");
        } else {
            System.out.println("***Failure*** Expired invitation code was incorrectly accepted.");
        }
    }
}