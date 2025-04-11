package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.ArrayList;

import databasePart1.DatabaseHelper;
    public class UserListPage {
        private final DatabaseHelper databaseHelper;
        private final User currentUser;

        public UserListPage(DatabaseHelper databaseHelper, User user) {
            this.databaseHelper = databaseHelper;
            this.currentUser = user;
        }

        public void show(Stage primaryStage) {
            VBox userLayout = new VBox(10);
            userLayout.setStyle("-fx-alignment: center; -fx-padding: 20;");

            Label titleLabel = new Label("User List");
            titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            VBox userDisplayContainer = new VBox(5);

            ArrayList<String> users = databaseHelper.getAllUsers();
            for (String user : users) {
                String[] parts = user.split("\\|");
                String username = parts[0].replace("Username:", "").trim();
                String email = parts[2].replace("Email:", "").trim();
                String roles = parts[3].replace("Role:", "").trim();

                Label userLabel = new Label("User: " + username + " | Email: " + email + " | Roles: " + roles);
                userDisplayContainer.getChildren().add(userLabel);
            }

            Button backButton = new Button("Back");
            backButton.setOnAction(e -> new AdminHomePage(databaseHelper).show(primaryStage, currentUser)); // Pass User

            userLayout.getChildren().addAll(titleLabel, userDisplayContainer, backButton);
            Scene userScene = new Scene(userLayout, 800, 400);

            primaryStage.setScene(userScene);
        }
    
    }