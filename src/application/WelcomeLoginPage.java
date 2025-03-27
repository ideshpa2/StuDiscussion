package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Platform;

import java.util.ArrayList;

import databasePart1.DatabaseHelper;

/**
 * The WelcomeLoginPage class displays a welcome screen for authenticated users.
 * It allows users to navigate to their respective pages based on their role or quit the application.
 */
public class WelcomeLoginPage {
	

    private final DatabaseHelper databaseHelper;

    // DatabaseHelper to handle database operations.
    public WelcomeLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

	
	public void show(Stage primaryStage, User user) {
	    
	    VBox layout = new VBox(5);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    Label welcomeLabel = new Label("Welcome!!");
	    welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    layout.getChildren().addAll(welcomeLabel);
	    
	    String username= user.getUserName();
        ArrayList<String> roles= databaseHelper.getUserRoles(username);
	    //If the user is new/ doesn't have any roles yet
	    if(roles.isEmpty()) {
	    	Label message= new Label("You haven't been assigned a role yet. Please check back later.");
	    	message.setStyle("-fx-font-size: 16px;");
	    	layout.getChildren().addAll(message);
	    }
	    // Button to navigate to the user's respective page based on their role
	    else {
        ComboBox<String> roleDropdown = new ComboBox<>();
        roleDropdown.getItems().addAll(roles);
        if (!roles.isEmpty()) {
            roleDropdown.setValue(roles.get(0)); // Select the first role by default
        }
	    Button continueButton = new Button("Select your role");
	    continueButton.setOnAction(a -> {String selectedRole = roleDropdown.getValue(); // Get the selected role

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
	    layout.getChildren().addAll(roleDropdown, continueButton);
	    }
	   
	    // Button to quit the application
	    Button quitButton = new Button("Quit");
	    quitButton.setOnAction(a -> {
	        databaseHelper.closeConnection();
	        Platform.exit(); // Exit the JavaFX application
	    });

	    // "Invite" button for admin to generate invitation codes
	    if (user.getRoles().contains("admin")) {  // Fix: Use .contains() instead of .equals()
	        Button inviteButton = new Button("Invite Others");
	        inviteButton.setOnAction(a -> {
	            new InvitationPage().show(databaseHelper, primaryStage, user);
	        });
	        layout.getChildren().add(inviteButton);
	    }

	    layout.getChildren().addAll(quitButton);
	    Scene welcomeScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(welcomeScene);
	    primaryStage.setTitle("Welcome Page");
	}
}