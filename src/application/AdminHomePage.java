package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class AdminHomePage {
    private final DatabaseHelper databaseHelper;
    
    public AdminHomePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
        //this.user = user;
    }

    public void show(Stage primaryStage, User user) {
        VBox layout = new VBox(10); // Add spacing
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        Label adminLabel = new Label("Hello, Admin!");
        adminLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Dropdown to select user for setting OTP
        ComboBox<String> userDropdown = new ComboBox<>();
        ArrayList<String> users = databaseHelper.getAllUsernames();
        userDropdown.getItems().addAll(users);

        // Button to set a one-time password
        Button oneTimePasswordButton = new Button("Set One-Time Password");
        Label otpResultLabel = new Label();

        oneTimePasswordButton.setOnAction(e -> {
            String selectedUser = userDropdown.getValue();
            if (selectedUser == null) {
                otpResultLabel.setText("Please select a user.");
                return;
            }

            String generatedOtp = UUID.randomUUID().toString().substring(0, 6); // Generate a short OTP
            try {
                databaseHelper.setOneTimePassword(selectedUser, generatedOtp);
                otpResultLabel.setText("One-Time Password set for " + selectedUser + ": " + generatedOtp);
            } catch (SQLException ex) {
                otpResultLabel.setText("Error setting OTP.");
                ex.printStackTrace();
            }
        });

        // Logout Button
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> {
            databaseHelper.closeConnection();
            new UserLoginPage(databaseHelper).show(primaryStage);
        });

        // Switch Role Button
        String username= user.getUserName();
        ArrayList<String> roles= databaseHelper.getUserRoles(username);
        ComboBox<String> roleDropdown = new ComboBox<>();
        roleDropdown.getItems().addAll(roles);
        if (!roles.isEmpty()) {
            roleDropdown.setValue(roles.get(0));
        }
        
        Button switchRole = new Button("Switch Role");
        switchRole.setOnAction(e -> {String selectedRole = roleDropdown.getValue();

        if (selectedRole != null) {
            switch (selectedRole.toLowerCase()) {
                case "student":
                    new StudentHomePage(databaseHelper).show(primaryStage, user);
                    break;
                case "staff":
                    new StaffHomePage(databaseHelper).show(primaryStage, user);
                    break;
                case "instructor":
                    new InstructorHomePage(databaseHelper).show(primaryStage, user);
                    break;
                case "admin":
                    new AdminHomePage(databaseHelper).show(primaryStage, user);
                    break;
                case "reviewer":
                    new ReviewerHomePage(databaseHelper).show(primaryStage, user);
                    break;
                case "user":
                    new UserHomePage(databaseHelper).show(primaryStage);
                    break;
                default:
                    System.out.println("Unknown role selected: " + selectedRole);
            }
        }
    });
        // Button to open User List Page
        Button listUsersButton = new Button("List All Users");
        listUsersButton.setOnAction(e -> new UserListPage(databaseHelper, user).show(primaryStage));

        // Button to open Role Assignment Page
        Button assignRoleButton = new Button("Assign User Role");
        assignRoleButton.setOnAction(e -> new AssignRolesPage(databaseHelper).show(primaryStage, user));

        // Button to delete users
        Button deleteButton = new Button("Delete Users");
        deleteButton.setOnAction(e -> new adminDeletePage(databaseHelper).show(primaryStage, user));

        // Add all UI elements
        layout.getChildren().addAll(
            adminLabel, userDropdown, oneTimePasswordButton, otpResultLabel,
            listUsersButton, assignRoleButton, deleteButton, roleDropdown, switchRole, logoutButton
        );

        // Set up the scene
        Scene adminScene = new Scene(layout, 800, 400);
        primaryStage.setScene(adminScene);
        primaryStage.setTitle("Admin Page");
        primaryStage.show();
    }
}