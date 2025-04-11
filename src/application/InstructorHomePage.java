package application;

import java.util.ArrayList;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * AdminPage class represents the user interface for the instructor user.
 * This page displays a simple welcome message for the instructor .
 */

public class InstructorHomePage {
	/**
     * Displays the instructor  page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
	
	private DatabaseHelper databaseHelper;
	
	public InstructorHomePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
        //this.user = user;
    }
	
    public void show(Stage primaryStage, User user) {
    	VBox layout = new VBox();
    	
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // label to display the welcome message for the instructor 
	    Label instructorLabel = new Label("Hello, Instructor!");
	    
	    instructorLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

	 // Logout Button
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> {
            databaseHelper.closeConnection();  // Close database connection
            new UserLoginPage(databaseHelper).show(primaryStage);  // Redirect to login
        });

     // Switch Role Button
        String username= user.getUserName();
        ArrayList<String> roles= databaseHelper.getUserRoles(username);
        ComboBox<String> roleDropdown = new ComboBox<>();
        roleDropdown.getItems().addAll(roles);
        if (!roles.isEmpty()) {
            roleDropdown.setValue(roles.get(0)); // Select the first role by default
        }
        
        Button switchRole = new Button("Switch Role");
        switchRole.setOnAction(e -> {String selectedRole = roleDropdown.getValue(); // Get the selected role

        if (selectedRole != null) {
            switch (selectedRole.toLowerCase()) { // Convert to lowercase for case-insensitive matching
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
                    System.out.println("Unknown role selected: " +  selectedRole);
            }
        }
    });

        
	    layout.getChildren().addAll(instructorLabel, logoutButton, roleDropdown, switchRole);
	    Scene instructorScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(instructorScene);
	    primaryStage.setTitle("instructor Page");
    }
}