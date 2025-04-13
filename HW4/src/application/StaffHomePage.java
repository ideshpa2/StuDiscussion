package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;


/**
 * StaffHomePage class provides the UI and functionality for the Staff user role.
 * Staff members can:
 * - View questions by tag
 * - Manage the probation list of reviewers
 * - Direct message users
 * - Switch roles
 * - Logout
 */
public class StaffHomePage {
    private DatabaseHelper databaseHelper;
    private User user;

    /**
     * Constructor to initialize the StaffHomePage with database access.
     * 
     * @param databaseHelper the DatabaseHelper instance for database operations
     */
    public StaffHomePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the main staff home page with options for various staff functionalities.
     * 
     * @param primaryStage the main window
     * @param user the currently logged-in staff user
     */
    public void show(Stage primaryStage, User user) {
        this.user = user;
        VBox layout = new VBox(15);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        Label title = new Label("Staff Homepage");

        Button viewByTagButton = new Button("View Questions by Tag");
        viewByTagButton.setOnAction(e -> showSearchByTagPage(primaryStage));

        Button manageProbationButton = new Button("Manage Probation List");
        manageProbationButton.setOnAction(e -> showProbationList(primaryStage));

        Button dmButton = new Button("Direct Message User");
        dmButton.setOnAction(e -> showDirectMessagePage(primaryStage));

        // Logout Button
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> {
            databaseHelper.closeConnection();
            new UserLoginPage(databaseHelper).show(primaryStage);
        });

        // Switch Role Dropdown and Button
        String username = user.getUserName();
        ArrayList<String> roles = databaseHelper.getUserRoles(username);
        ComboBox<String> roleDropdown = new ComboBox<>();
        roleDropdown.getItems().addAll(roles);
        if (!roles.isEmpty()) {
            roleDropdown.setValue(roles.get(0)); // Select the first role
        }

        Button switchRoleButton = new Button("Switch Role");
        switchRoleButton.setOnAction(e -> {
            String selectedRole = roleDropdown.getValue();
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

        layout.getChildren().addAll(
            title, 
            viewByTagButton, 
            manageProbationButton, 
            dmButton, 
            new Label("Switch Role:"), 
            roleDropdown, 
            switchRoleButton, 
            logoutButton
        );

        Scene scene = new Scene(layout, 800, 400);
        primaryStage.setScene(scene);
    }
  

    /**
     * Displays the UI for viewing questions by selected tag.
     * Allows staff to delete answers and reviews.
     * 
     * @param primaryStage the main window
     */
    private void showSearchByTagPage(Stage primaryStage) {
        VBox layout = new VBox(15);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        Label titleLabel = new Label("Search Questions by Tag:");

        HBox tagButtonContainer = new HBox(10);
        tagButtonContainer.setStyle("-fx-alignment: center;");

        Button assignmentsButton = new Button("Assignments");
        Button examsButton = new Button("Exams");
        Button generalButton = new Button("General");

        tagButtonContainer.getChildren().addAll(assignmentsButton, examsButton, generalButton);

        VBox questionContainer = new VBox(10);
        ScrollPane scrollPane = new ScrollPane(questionContainer);
        scrollPane.setFitToWidth(true);

        EventHandler<ActionEvent> searchHandler = event -> {
            Button clickedButton = (Button) event.getSource();
            String selectedTag = clickedButton.getText();

            try {
                questionContainer.getChildren().clear();
                List<Question> questions = databaseHelper.getQuestionsByTag(selectedTag);

                for (Question q : questions) {
                    VBox questionBox = new VBox(5);
                    String askedBy = q.getUser().getUserName();
                    String questionText = q.isResolved() ? "✅ " + q.getContent() : q.getContent();
                    Label questionLabel = new Label(questionText + " [" + q.getTags() + "] (asked by " + askedBy + ")");

                    VBox answersBox = new VBox(5);
                    List<Answer> answers = databaseHelper.getAnswersByQuestionId(q.getId());

                    for (Answer a : answers) {
                        String answerText = a.toString();
                        Label answerLabel = new Label("→ " + answerText + " (answered by " + a.getUser().getUserName() + ")");

                        Button deleteAnswerButton = new Button("Delete Answer");
                        deleteAnswerButton.setOnAction(e -> {
                            try {
                                databaseHelper.deleteAnswer(a.getId());
                                showSearchByTagPage(primaryStage); // Refresh page
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }
                        });

                        List<Review> reviews = databaseHelper.getReviewsByAnswerId(a.getId());
                        VBox reviewsBox = new VBox(5);
                        for (Review r : reviews) {
                            Label reviewLabel = new Label("Review: " + r.getContent());
                            Button deleteReviewButton = new Button("Delete Review");
                            deleteReviewButton.setOnAction(e -> {
                                try {
                                    databaseHelper.deleteReview(r.getId());
                                    showSearchByTagPage(primaryStage); // Refresh page
                                } catch (SQLException ex) {
                                    ex.printStackTrace();
                                }
                            });
                            reviewsBox.getChildren().addAll(reviewLabel, deleteReviewButton);
                        }

                        answersBox.getChildren().addAll(answerLabel, deleteAnswerButton, reviewsBox);
                    }

                    questionBox.getChildren().addAll(questionLabel, answersBox);
                    questionBox.setStyle("-fx-border-color: black; -fx-padding: 10; -fx-background-color: #f5f5f5;");
                    questionContainer.getChildren().add(questionBox);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        };

        assignmentsButton.setOnAction(searchHandler);
        examsButton.setOnAction(searchHandler);
        generalButton.setOnAction(searchHandler);

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> show(primaryStage, user));

        layout.getChildren().addAll(titleLabel, tagButtonContainer, scrollPane, backButton);
        primaryStage.setScene(new Scene(layout, 800, 400));
    }

    /**
     * Displays the probation list of reviewers.
     * Staff can revoke reviewer role or add new users to the probation list.
     * 
     * @param primaryStage the main window
     */
    private void showProbationList(Stage primaryStage) {
        VBox layout = new VBox(15);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        
        Label titleLabel = new Label("Manage Probation List");

        VBox probationContainer = new VBox(10);
        ScrollPane scrollPane = new ScrollPane(probationContainer);
        scrollPane.setFitToWidth(true);

        try {
            List<User> probationUsers = databaseHelper.getProbationList();
            for (User user : probationUsers) {
                HBox userBox = new HBox(10);
                Label userLabel = new Label("Reviewer: " + user.getUserName());

                Button revokeButton = new Button("Revoke Reviewer Role");
                revokeButton.setOnAction(e -> {
                    try {
                        databaseHelper.revokeReviewerRole(user.getId());
                        showProbationList(primaryStage); // Refresh the page
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                });

                userBox.getChildren().addAll(userLabel, revokeButton);
                probationContainer.getChildren().add(userBox);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Add to Probation List Section
        Label addLabel = new Label("Enter Username to Add to Probation:");
        TextField usernameField = new TextField();
        Button addButton = new Button("Add");

        addButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            try {
                User targetUser = databaseHelper.getUserByUserName(username);
                if (targetUser != null) {
                    databaseHelper.addToProbationList(targetUser.getId());
                    showProbationList(primaryStage); // Refresh
                } else {
                    System.out.println("User not found.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> show(primaryStage, user));

        layout.getChildren().addAll(titleLabel, scrollPane, addLabel, usernameField, addButton, backButton);
        primaryStage.setScene(new Scene(layout, 800, 400));
    }

    /**
     * Displays the direct messaging page.
     * Allows staff to send a direct message to any user by username.
     * 
     * @param primaryStage the main window
     */
    private void showDirectMessagePage(Stage primaryStage) {
        VBox layout = new VBox(10);
        Label title = new Label("Send Direct Message");

        TextField recipientField = new TextField();
        recipientField.setPromptText("Enter username");

        TextArea messageArea = new TextArea();
        messageArea.setPromptText("Enter message...");

        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> {
            String recipient = recipientField.getText();
            String message = messageArea.getText();
            try {
                User recipientUser = databaseHelper.getUserByUserName(recipient);
                if (recipientUser != null) {
                    databaseHelper.sendMessage(user.getId(), recipientUser.getId(), message);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> show(primaryStage, user));

        layout.getChildren().addAll(title, recipientField, messageArea, sendButton, backButton);
        primaryStage.setScene(new Scene(layout, 800, 400));
    }
}
