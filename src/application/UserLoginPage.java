package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

import databasePart1.*;

/**
 * The UserLoginPage class provides a login interface for users to access their accounts.
 * It validates the user's credentials and navigates to the appropriate page upon successful login.
 */
public class UserLoginPage {
    
    private final DatabaseHelper databaseHelper;
 
    public UserLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
        // Input fields for the user's username and password
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter Username");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);

        // Label to display error messages
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        // Forgot password button
        Button forgotButton = new Button("Forgot Password");
        forgotButton.setOnAction(e -> {
            String userName = userNameField.getText();
            if (userName.isEmpty()) {
                errorLabel.setText("Enter a valid username");
            } else {
                new ChangePasswordPage(databaseHelper, userName).show(primaryStage);
            }
        });

        Button loginButton = new Button("Login");

        loginButton.setOnAction(a -> {
            try {
				databaseHelper.ensureConnectionOpen();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
            // Retrieve user inputs
            String userName = userNameField.getText();
            String password = passwordField.getText();

            try {
                // Retrieve the user's roles from the database
                List<String> roles = databaseHelper.getUserRoles(userName);

                // Check if user needs to log in with OTP first
                if (databaseHelper.isOtpLogin(userName, password)) {
                    new ChangePasswordPage(databaseHelper, userName).show(primaryStage);
                    return; // Stop execution
                }
                databaseHelper.increment_num_users();
                User user = new User(databaseHelper.get_num_users(),userName, password, "user@example.com", roles);

                //User has no roles
                if (roles == null || roles.isEmpty()) {
                	new WelcomeLoginPage(databaseHelper).show(primaryStage, user);
                    return; // Stop execution
                }
                // User has only one role
                if (roles.size() == 1) { 
                    String role = roles.get(0);
                    //databaseHelper.increment_num_users();
                    //User tempUser = new User(databaseHelper.get_num_users(),userName, password, "user@example.com", List.of(role));
                    User u = databaseHelper.getUserByUserName(userName);
                    if (databaseHelper.login(u)) {
                        switch (role.toLowerCase()) {
                            case "admin" -> new AdminHomePage(databaseHelper).show(primaryStage, u);
                            case "student" -> new StudentHomePage(databaseHelper).show(primaryStage, u);
                            case "instructor" -> new InstructorHomePage(databaseHelper).show(primaryStage, u);
                            case "staff" -> new StaffHomePage(databaseHelper).show(primaryStage, u);
                            case "reviewer" -> new ReviewerHomePage(databaseHelper).show(primaryStage, u);
                            default -> errorLabel.setText("Unknown role.");
                        }
                        return; // Stop execution
                    } else {
                        errorLabel.setText("Invalid username or password.");
                        return; // Stop execution
                    }
                }

                // Handle multiple roles by sending the user to a generic page
                new WelcomeLoginPage(databaseHelper).show(primaryStage, user);

            } catch (SQLException e) {
                errorLabel.setText("Database error occurred. Try again later.");
                e.printStackTrace();
            }
        });

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(userNameField, passwordField, loginButton, forgotButton, errorLabel);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("User Login");
        primaryStage.show();
    }
}