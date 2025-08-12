package application;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;

public class showTrustedReviewersPage {
    private DatabaseHelper databaseHelper;

    public showTrustedReviewersPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage, User user) {
        VBox layout = new VBox(15);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        
        Label titleLabel = new Label("Trusted Reviewers");
        titleLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
        
        VBox trustedReviewersContainer = new VBox(10);
        VBox potentialReviewersContainer = new VBox(10);
        potentialReviewersContainer.setVisible(false); // Initially hidden

        try {
            // Fetch trusted reviewers
        	Map<User, Integer> currentTrustedReviewers = databaseHelper.getTrustedReviewersForStudent(user.getId());
            List<User> allUsers = databaseHelper.getAllUserObjects();
            List<User> potentialReviewers = new ArrayList<>();

            if (currentTrustedReviewers.isEmpty()) {
                trustedReviewersContainer.getChildren().add(new Label("No trusted reviewers found."));
            } else {
            	for (Map.Entry<User, Integer> entry : currentTrustedReviewers.entrySet()) {
            	    User reviewer = entry.getKey();
            	    int weight = entry.getValue();

            	    HBox reviewerBox = new HBox(10);
            	    Label reviewerName = new Label(reviewer.getUserName() + " (Weight: " + weight + ")");
            	    
            	    // Optional: Let user change weight
            	    TextField weightField = new TextField(String.valueOf(weight));
            	    weightField.setPrefWidth(50);
            	    
            	    Button updateWeightButton = new Button("Update");
            	    updateWeightButton.setOnAction(e -> {
            	        try {
            	            int newWeight = Integer.parseInt(weightField.getText());
            	            databaseHelper.updateReviewerWeight(user.getId(), reviewer.getId(), newWeight);
            	            new showTrustedReviewersPage(databaseHelper).show(primaryStage, user);
            	        } catch (SQLException | NumberFormatException ex) {
            	            ex.printStackTrace();
            	        }
            	    });

            	    Button removeButton = new Button("Remove");
            	    removeButton.setOnAction(e -> {
            	        try {
            	            databaseHelper.removeTrustedReviewer(user.getId(), reviewer.getId());
            	            new showTrustedReviewersPage(databaseHelper).show(primaryStage, user);
            	        } catch (SQLException ex) {
            	            ex.printStackTrace();
            	        }
            	    });

            	    reviewerBox.getChildren().addAll(reviewerName, weightField, updateWeightButton, removeButton);
            	    trustedReviewersContainer.getChildren().add(reviewerBox);
            	}
            }

            // Add "Add More" button to reveal potential reviewers
            Button addMoreButton = new Button("Add More");
            addMoreButton.setOnAction(e -> potentialReviewersContainer.setVisible(true));

            // Identify potential reviewers (those not already trusted)
            for (User u : allUsers) {
                if (u.getRoles().contains("Reviewer") && u.getId() != user.getId()) {
                    boolean isTrusted = false;
                    for (User trusted : currentTrustedReviewers.keySet()) {
                        if (trusted.getId() == u.getId()) {
                            isTrusted = true;
                            break;
                        }
                    }
                    if (!isTrusted) {
                        potentialReviewers.add(u);
                    }
                }
            }

            if (!potentialReviewers.isEmpty()) {
                Label selectLabel = new Label("Select a reviewer to add:");
                potentialReviewersContainer.getChildren().add(selectLabel);

                for (User reviewer : potentialReviewers) {
                    HBox reviewerBox = new HBox(10);
                    Label reviewerName = new Label(reviewer.getUserName());
                    TextField weightField = new TextField("1");
                    weightField.setPrefWidth(50);

                    Button addButton = new Button("Add");
                    addButton.setOnAction(e -> {
                        try {
                            int weight = Integer.parseInt(weightField.getText());
                            databaseHelper.addTrustedReviewer(user.getId(), reviewer.getId(), weight);
                            new showTrustedReviewersPage(databaseHelper).show(primaryStage, user);
                        } catch (SQLException | NumberFormatException ex) {
                            ex.printStackTrace();
                        }
                    });

                    reviewerBox.getChildren().addAll(reviewerName, weightField, addButton);
                    potentialReviewersContainer.getChildren().add(reviewerBox);
                }
            } else {
                potentialReviewersContainer.getChildren().add(new Label("No available reviewers to add."));
            }

            // Back button
            Button backButton = new Button("Back");
            backButton.setOnAction(e -> new StudentHomePage(databaseHelper).show(primaryStage, user));

            layout.getChildren().addAll(titleLabel, trustedReviewersContainer, addMoreButton, potentialReviewersContainer, backButton);
            primaryStage.setScene(new Scene(layout, 800, 400));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
