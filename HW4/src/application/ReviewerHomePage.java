package application;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;

/**
     * The {@code ReviewerHomePage} class represents the home page for a reviewer.
     * <p>
     * It provides functionality for displaying questions, user reviews, searching questions by tag,
     * switching roles, and writing/editing reviews. This class interacts with the {@link DatabaseHelper}
     * to retrieve and update data from the database.
     * </p>
     */

public class ReviewerHomePage {
    private DatabaseHelper databaseHelper;
    private User user;
    private final SortQuestions sortQuestions;
    private final SortAnswers sortAnswers;

    public ReviewerHomePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
        this.sortQuestions = new SortQuestions(this.databaseHelper);
        this.sortAnswers = new SortAnswers(this.databaseHelper);
    }

    /**
     * Displays the reviewer home page.
     * <p>
     * The page greets the user, provides buttons to view all questions, view user reviews,
     * search questions by tag, and logout. It also displays a role dropdown for switching roles.
     * </p>
     *
     * @param primaryStage the primary stage of the application
     * @param user         the current logged-in user
     */
    
    public void show(Stage primaryStage, User user) {
        VBox layout = new VBox();
        this.user = user;
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        Label studentLabel = new Label("Hello, " + user.getUserName() + "!");
        studentLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button listQuestionsButton = new Button("View All Questions");
        Button viewMyReviewsButton = new Button("View User Reviews");
        Button searchByTagButton = new Button("Search Questions by Tag");

        listQuestionsButton.setOnAction(e -> showListQuestionsPage(primaryStage));
        viewMyReviewsButton.setOnAction(e -> showUserReviews(primaryStage));
        searchByTagButton.setOnAction(e -> showSearchByTagPage(primaryStage));
        Button viewFeedbackButton = new Button("View Feedback");
        viewFeedbackButton.setOnAction(e -> showFeedbackPage(primaryStage, user));
        layout.getChildren().add(viewFeedbackButton);
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> {
            databaseHelper.closeConnection();
            new UserLoginPage(databaseHelper).show(primaryStage);
        });

        ArrayList<String> roles = databaseHelper.getUserRoles(user.getUserName());
        ComboBox<String> roleDropdown = new ComboBox<>();
        roleDropdown.getItems().addAll(roles);
        if (!roles.isEmpty()) {
            roleDropdown.setValue(roles.get(0));
        }

        Button switchRole = new Button("Switch Role");
        switchRole.setOnAction(e -> {
            String selectedRole = roleDropdown.getValue();
            if (selectedRole != null) {
                switch (selectedRole.toLowerCase()) {
                    case "student": new StudentHomePage(databaseHelper).show(primaryStage, user); break;
                    case "staff": new StaffHomePage(databaseHelper).show(primaryStage, user); break;
                    case "instructor": new InstructorHomePage(databaseHelper).show(primaryStage, user); break;
                    case "admin": new AdminHomePage(databaseHelper).show(primaryStage, user); break;
                    case "reviewer": new ReviewerHomePage(databaseHelper).show(primaryStage, user); break;
                    case "user": new UserHomePage(databaseHelper).show(primaryStage); break;
                    default: System.out.println("Unknown role selected: " + selectedRole);
                }
            }
        });

        layout.getChildren().addAll(studentLabel, searchByTagButton, viewMyReviewsButton, logoutButton, roleDropdown, switchRole);
        Scene scene = new Scene(layout, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Reviewer Page");
    }

    /**
     * Displays the page showing the current user's reviews.
     * <p>
     * Reviews are fetched from the database and displayed in a scrollable layout.
     * </p>
     *
     * @param primaryStage the primary stage of the application
     */
    
    private void showUserReviews(Stage primaryStage) {
        VBox layout = new VBox(15);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        Label titleLabel = new Label("My Reviews");
        VBox reviewContainer = new VBox(10);
        ScrollPane scrollPane = new ScrollPane(reviewContainer);
        scrollPane.setFitToWidth(true);

        try {
            List<Review> reviews = databaseHelper.getReviewsByReviewer(user.getId());
            for (Review r : reviews) {
                String answerContent = r.getAnswer() != null ? r.getAnswer().getContent() : "(Answer not found)";
                VBox reviewBox = new VBox(5);
                Label reviewLabel = new Label("Review on Answer: \"" + answerContent + "\" → " + r.getContent());
                Button editButton = new Button("Edit Review");               
                editButton.setOnAction(e -> showEditReviewForm(primaryStage, r));
                reviewBox.getChildren().addAll(reviewLabel, editButton);
                reviewContainer.getChildren().add(reviewBox);
            }
        } catch (SQLException ex) {
            reviewContainer.getChildren().add(new Label("Failed to load reviews."));
            ex.printStackTrace();
        }

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> show(primaryStage, user));

        layout.getChildren().addAll(titleLabel, scrollPane, backButton);
        primaryStage.setScene(new Scene(layout, 800, 400));
    }
    
    private void showEditReviewForm(Stage primaryStage, Review review) {
        VBox layout = new VBox(15);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        Label instructionLabel = new Label("Edit Review for: " + review.getAnswer().getContent());
        TextArea reviewInput = new TextArea(review.getContent());
        reviewInput.setPromptText("Edit your review...");

        Button submitButton = new Button("Save Changes");
        submitButton.setOnAction(e -> {
            String newContent = reviewInput.getText().trim();
            if (!newContent.isEmpty()) {
                try {
                    review.setContent(newContent);
                    databaseHelper.updateReview(review);
                    showUserReviews(primaryStage);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        Button backButton = new Button("Cancel");
        backButton.setOnAction(e -> showUserReviews(primaryStage));

        layout.getChildren().addAll(instructionLabel, reviewInput, submitButton, backButton);
        primaryStage.setScene(new Scene(layout, 800, 400));
    }

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
                        VBox answerBox = new VBox(3);
                        Label answerLabel = new Label("→ " + a.toString() + " (answered by " + a.getUser().getUserName() + ")");

                        if (a.isSolution()) {
                            answerLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold; -fx-padding: 5; -fx-background-color: #e8f5e9;");
                        } else {
                            answerLabel.setStyle("-fx-padding: 5; -fx-background-color: #e8e8e8;");
                        }

                        Button reviewButton = new Button("Add Review");
                        reviewButton.setOnAction(e -> showWriteReviewForm(primaryStage, a));

                        VBox reviewsBox = new VBox(3);
                        try {
                            List<Review> reviews = databaseHelper.getReviewsByAnswerId(a.getId());
                            for (Review r : reviews) {
                                Label reviewLabel = new Label("   ↳ Review: " + r.getContent() + " (by " + r.getReviewer().getUserName() + ")");
                                reviewsBox.getChildren().add(reviewLabel);
                            }
                        } catch (SQLException ex) {
                            reviewsBox.getChildren().add(new Label("Failed to load reviews."));
                        }

                        answerBox.getChildren().addAll(answerLabel, reviewButton, reviewsBox);
                        answersBox.getChildren().add(answerBox);
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


    private void showListQuestionsPage(Stage primaryStage) {
    	// ... [UNCHANGED: Same as earlier] ...
    }

    /**
     * Displays the form for writing a new review for a specific answer.
     * <p>
     * Once submitted, the review is added to the database and the reviewer home page is shown.
     * </p>
     *
     * @param primaryStage the primary stage of the application
     * @param answer       the answer for which the review is being written
     */
    
    private void showWriteReviewForm(Stage primaryStage, Answer answer) {
        VBox layout = new VBox(15);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        Label instructionLabel = new Label("Write a Review for: " + answer.getContent());
        TextArea reviewInput = new TextArea();
        reviewInput.setPromptText("Enter your review...");

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            String content = reviewInput.getText().trim();
            if (!content.isEmpty()) {
                try {
                    Review review = new Review(0, content, user, answer, null);
                    databaseHelper.addReview(review);
                    show(primaryStage, user);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> show(primaryStage, user));

        layout.getChildren().addAll(instructionLabel, reviewInput, submitButton, backButton);
        primaryStage.setScene(new Scene(layout, 800, 400));
    }
    
    private void showFeedbackPage(Stage primaryStage, User reviewer) {
        VBox layout = new VBox(15);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        Label titleLabel = new Label("Feedback Received");

        VBox feedbackContainer = new VBox(10);
        ScrollPane scrollPane = new ScrollPane(feedbackContainer);
        scrollPane.setFitToWidth(true);

        try {
            List<Feedback> feedbackList = databaseHelper.getFeedbackForReviewer(reviewer.getId());
            if (feedbackList.isEmpty()) {
                feedbackContainer.getChildren().add(new Label("No feedback received yet."));
            } else {
                for (Feedback feedback : feedbackList) {
                    Label feedbackLabel = new Label("- " + feedback.getStudent().getUserName() + ": " + feedback.getContent() + " for Review to Answer :" + (databaseHelper.getAnswerById(feedback.getAnswerId())).getContent());
                    feedbackContainer.getChildren().add(feedbackLabel);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            feedbackContainer.getChildren().add(new Label("Error loading feedback."));
        }

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> show(primaryStage, user));

        layout.getChildren().addAll(titleLabel, scrollPane, backButton);
        primaryStage.setScene(new Scene(layout, 600, 400));
    }
}

