package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;

public class AssignRolesPage {
    private final DatabaseHelper databaseHelper;

    public AssignRolesPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage, User user) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Label titleLabel = new Label("Manage User Roles");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Dropdown to select user
        ComboBox<String> userDropdown = new ComboBox<>();
        ArrayList<String> users = databaseHelper.getAllUsernames();
        userDropdown.getItems().addAll(users);

        // Dropdown to select role
        ComboBox<String> roleDropdown = new ComboBox<>();
        roleDropdown.getItems().addAll("Admin", "Student", "Instructor", "Staff", "Reviewer");

        // Assign Role Button
        Button assignRoleButton = new Button("Assign Role");
        
        // Remove Role Button
        Button removeRoleButton = new Button("Remove Role");
        
        Label resultLabel = new Label();

        // Assign Role Action
        assignRoleButton.setOnAction(e -> {
            String selectedUser = userDropdown.getValue();
            String selectedRole = roleDropdown.getValue();

            if (selectedUser == null || selectedRole == null) {
                resultLabel.setText("Please select a user and a role.");
                return;
            }

            try {
                // Retrieve existing roles
                ArrayList<String> roles = databaseHelper.getUserRoles(selectedUser);

                // Add the new role if not already assigned
                if (!roles.contains(selectedRole)) {
                    databaseHelper.addUserRole(selectedUser, selectedRole); // Use SQL method
                    resultLabel.setText("Role assigned successfully!");
                } else {
                    resultLabel.setText("User already has this role.");
                }
            } catch (SQLException ex) {
                resultLabel.setText("Error assigning role.");
                ex.printStackTrace();
            }
        });

     // Remove Role Action
        removeRoleButton.setOnAction(e -> {
            String selectedUser = userDropdown.getValue();
            String selectedRole = roleDropdown.getValue();

            if (selectedUser == null || selectedRole == null) {
                resultLabel.setText("Please select a user and a role.");
                return;
            }

            try {
                // Call the SQL method to remove the role
                databaseHelper.removeUserRole(selectedUser, selectedRole);
                resultLabel.setText("Role removed successfully!");
            } catch (SQLException ex) {
                resultLabel.setText(ex.getMessage()); // Show SQL error message
                ex.printStackTrace();
            }
        });
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> new AdminHomePage(databaseHelper).show(primaryStage, user));
        layout.getChildren().addAll(titleLabel, userDropdown, roleDropdown, assignRoleButton, removeRoleButton, resultLabel, backButton);
        Scene assignRoleScene = new Scene(layout, 800, 400);
        primaryStage.setScene(assignRoleScene);
        primaryStage.setTitle("Manage User Roles");
    }
}