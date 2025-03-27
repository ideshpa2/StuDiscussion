package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;

import databasePart1.*;

/**
 * SetupAccountPage class handles the account setup process for new users.
 * Users provide their userName, password, and a valid invitation code to register.
 */
public class SetupAccountPage {
	public int num_users = 0;

    private final DatabaseHelper databaseHelper;

    // DatabaseHelper to handle database operations.
    public SetupAccountPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the Setup Account page in the provided stage.
     *
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
        // Input fields for userName, password, and invitation code
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(250);

        TextField passwordField = new TextField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);

        /*TextField userRoleField = new TextField();
        userRoleField.setPromptText("Enter userRole");
        userRoleField.setMaxWidth(250);*/

        TextField inviteCodeField = new TextField();
        inviteCodeField.setPromptText("Enter Invitation Code");
        inviteCodeField.setMaxWidth(250);

        // Label to display error messages for invalid input or registration issues
        Label userNameErrorLabel = new Label();
        
        Label passwordErrorLabel = new Label();

        Label userRoleErrorLabel = new Label();
        
        Label errorLabel = new Label();

        passwordErrorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        
        userNameErrorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        userRoleErrorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
            
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Button setupButton = new Button("Setup");

        setupButton.setOnAction(a -> {
        	
            // Retrieve user input
            String userName = userNameField.getText().trim();
            String password = passwordField.getText().trim();
            String code = inviteCodeField.getText().trim();
            //String userRole = userRoleField.getText();

            // Validate the username by importing from UserNameRecognizer class and storing the error message in userNameError
            // Pass userName into checkForValidUserName method
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

            // Stop processing input for now if there are errors
            if (hasErrors) {
                return;
            }
            

            try {
                // Check if the user already exists
                if (!databaseHelper.doesUserExist(userName)) {
                    
                    // Validate the invitation code
                    if (databaseHelper.validateInvitationCode(code)) {
                        
                        // Store the user's role in a list
                        ArrayList<String> roles = new ArrayList<>();
                        //roles.add("user");
                        
                        // Create a new user with multiple role support
                        databaseHelper.increment_num_users();
                        User user = new User(databaseHelper.get_num_users(), userName, password, "user@example.com", roles); // Add an email parameter

                        // Register user in the database
                        databaseHelper.register(user, roles);

                        // Navigate to the Welcome Login Page
                        new WelcomeLoginPage(databaseHelper).show(primaryStage, user);
                    } else {
                        errorLabel.setText("Please enter a valid invitation code.");
                    }
                } else {
                    errorLabel.setText("This username is taken! Please choose another.");
                }
            } catch (SQLException e) {
                errorLabel.setText("Database error occurred. Try again later.");
                e.printStackTrace();
            }
        }); 

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(userNameField, passwordField, inviteCodeField, setupButton, passwordErrorLabel, userNameErrorLabel,  userRoleErrorLabel);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Account Setup");
        primaryStage.show(); 
    
        }
    }