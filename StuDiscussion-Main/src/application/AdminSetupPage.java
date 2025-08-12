package application;

import javafx.scene.Scene; 
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import databasePart1.DatabaseHelper;

/**
 * The SetupAdmin class handles the setup process for creating an administrator account.
 * This is intended to be used by the first user to initialize the system with admin credentials.
 */
public class AdminSetupPage {
	
    private final DatabaseHelper databaseHelper;

    public AdminSetupPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
    	// Input fields for userName, password, and email
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter Admin Username");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);

        TextField emailField = new TextField();
        emailField.setPromptText("Enter Email");
        emailField.setMaxWidth(250);

	    /*TextField userRoleField = new TextField();
        userRoleField.setPromptText("Enter User Role");
        userRoleField.setMaxWidth(250);*/
        
        Label userNameErrorLabel = new Label();
        Label passwordErrorLabel = new Label();
        Label emailErrorLabel = new Label();
	    //Label userRoleErrorLabel = new Label();  
        
        userNameErrorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        passwordErrorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        emailErrorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        //userRoleErrorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
		
        Button setupButton = new Button("Setup");

        setupButton.setOnAction(a -> {
            // Retrieve user input
            String userName = userNameField.getText();
            String password = passwordField.getText();
            String email = emailField.getText();
            //String userRole = roleDropdown.getText();

            String userNameError = UserNameRecognizer.checkForValidUserName(userName);
            String passwordError = PasswordEvaluator.evaluatePassword(password);

            boolean hasErrors = false;

            // Handle username validation
            if (!userNameError.isEmpty()) {
                userNameErrorLabel.setText("Username Error: " + userNameError);
                hasErrors = true;
            } else {
                userNameErrorLabel.setStyle("-fx-text-fill: green; -fx-font-size: 12px;");
                userNameErrorLabel.setText("Username is valid.");
            }

            // Handle password validation
            if (!passwordError.isEmpty()) {
                passwordErrorLabel.setText("Password Error: " + passwordError);
                hasErrors = true;
            } else {
                passwordErrorLabel.setStyle("-fx-text-fill: green; -fx-font-size: 12px;");
                passwordErrorLabel.setText("Password is valid.");
            }

            // Validate email field (basic check)
            if (email.isEmpty() || !email.contains("@")) {
                emailErrorLabel.setText("Invalid Email Address");
                hasErrors = true;
            } else {
                emailErrorLabel.setStyle("-fx-text-fill: green; -fx-font-size: 12px;");
                emailErrorLabel.setText("Email is valid.");
            }

		/*if (userRole.isEmpty() || (!userRole.equals("admin") || !userRole.equals("student") ||!userRole.equals("reviewer") || !userRole.equals("staff") ||  !userRole.equals("instructor"))) {
                    userRoleErrorLabel.setText("Invalid user role");
                    hasErrors = true;
             } else {
                    userRoleErrorLabel.setStyle("-fx-text-fill: green; -fx-font-size: 12px;");
                    userRoleErrorLabel.setText("User role is valid.");
             }*/

            // Stop further processing if there are errors
            if (hasErrors) {
                return;
            }

            try {
                // Create a new User object with admin role
                ArrayList<String> roles = new ArrayList<>(Arrays.asList("admin")); // Convert to ArrayList
                databaseHelper.increment_num_users();
                User user = new User(databaseHelper.get_num_users(),userName, password, email, roles);

                // Register user in the database
                databaseHelper.register(user, roles); // Pass both user and roles
                System.out.println("Administrator setup completed.");
                
                // Navigate to the Welcome Login Page
                new WelcomeLoginPage(databaseHelper).show(primaryStage, user);
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        });

        VBox layout = new VBox(10, userNameField, passwordField, emailField, setupButton, userNameErrorLabel, passwordErrorLabel, emailErrorLabel);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Administrator Setup");
        primaryStage.show();
    }
}
