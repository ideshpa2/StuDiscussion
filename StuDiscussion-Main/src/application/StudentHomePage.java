package application;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import databasePart1.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * StudentPage class represents the user interface for the student user.
 * This page displays a simple welcome message for the student.
 */

public class StudentHomePage {
	/**
     * Displays the student page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
	
	private DatabaseHelper databaseHelper;
	private User user;
	private final SortQuestions sortQuestions;
	private final SortAnswers sortAnswers;
	private List<String> trustedReviewers;

public StudentHomePage(DatabaseHelper databaseHelper) {
    this.databaseHelper = databaseHelper;
    //this.user = user;
    this.sortQuestions = new SortQuestions(databaseHelper);
    this.sortAnswers = new SortAnswers(databaseHelper);
}

	/**
 * Displays the student home page.
 * <p>
 * This method creates the main layout with buttons for asking a question,
 * viewing questions and answers, searching by tag, viewing reviewers,
 * switching roles, and logging out.
 * </p>
 *
 * @param primaryStage the primary stage where the scene will be displayed
 * @param user         the current logged-in user
 */
	
    public void show(Stage primaryStage, User user) {
    	VBox layout = new VBox();
    	this.user = user;
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // label to display the welcome message for the student
	    Label studentLabel = new Label("Hello, " + user.getUserName() + "!");
        studentLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        Button askQuestionButton = new Button("Ask a Question");
        Button listQuestionsButton = new Button("View All Questions");
        Button viewMyQuestionsButton = new Button("View My Questions"); // NEW
        Button searchByTagButton = new Button("Search Questions by Tag");
        Button viewMyAnswersButton = new Button("View My Answers");
        Button viewTrustedReviewers = new Button("View Trusted Reviewers List");
        Button viewUpdates = new Button("View Updates");
        viewUpdates.setOnAction(e -> new ViewReviewUpdatesPage(databaseHelper).show(primaryStage, user));
        viewTrustedReviewers.setOnAction(e -> new showTrustedReviewersPage(databaseHelper).show(primaryStage,user));
        viewMyAnswersButton.setOnAction(e -> {
			try {
				showUserAnswersPage(primaryStage, user);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		});
        Button searchButton = new Button("Search");

	//search button
        searchButton.setOnAction(e -> showSearchPage(primaryStage, user));
	    
        viewMyAnswersButton.setOnAction(e -> {
			try {
				showUserAnswersPage(primaryStage, user);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		});
        askQuestionButton.setOnAction(e -> showAskQuestionPage(primaryStage));
        listQuestionsButton.setOnAction(e -> showListQuestionsPage(primaryStage));
        viewMyQuestionsButton.setOnAction(e -> {
			try {
				showUserQuestionsPage(primaryStage, user);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}); // NEW

	    //reviewer list button
        /*viewReviewersButton.setOnAction(e -> {
			showUserReviewersPage(primaryStage, user);
		});*/
	    
        searchByTagButton.setOnAction(e -> showSearchByTagPage(primaryStage));
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

        Label reviewLabel = new Label();
        Button requestReviewer = new Button("Click here to request to be a reviewer");
        requestReviewer.setOnAction(e->
        {
        	try{
        		if(!databaseHelper.getUserRoles(user.getUserName()).contains("Reviewer") && !databaseHelper.requestedReviewer(user))
        		{
        			databaseHelper.requestToBeReviewer(user);
        			reviewLabel.setText("Your request will be processed by an instructor shortly");
        		}else if(databaseHelper.requestedReviewer(user)){
        			reviewLabel.setText("You have already requested to be reviewer! Please be patient while an instructor reviews your request!");
        		}else if(databaseHelper.getUserRoles(user.getUserName()).contains("Reviewer")) {
        			reviewLabel.setText("You already possess the role of Reviewer!");
        		}
        	}catch(Exception ex)
        	{
        		System.out.println("Error exception thrown");
        		System.out.println(ex.getMessage());
        	}
        });
	    layout.getChildren().addAll(studentLabel, askQuestionButton, viewMyQuestionsButton, viewMyAnswersButton, viewTrustedReviewers, viewUpdates, searchByTagButton, searchButton, logoutButton, roleDropdown, switchRole, requestReviewer,reviewLabel);
	    Scene studentScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(studentScene);
	    primaryStage.setTitle("Student Page");
    }
	    
	    
	    
	    
	    private void showAskQuestionPage(Stage primaryStage) {
	        VBox layout = new VBox(15);
	        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

	        Label titleLabel = new Label("Ask a Question");
	        TextField questionField = new TextField();
	        questionField.setPromptText("Enter your question...");

	        Label errorLabel = new Label();
	        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

	        // Buttons for predefined tags
	        ToggleGroup tagGroup = new ToggleGroup();
	        RadioButton assignmentTag = new RadioButton("Assignments");
	        RadioButton examTag = new RadioButton("Exams");
	        RadioButton generalTag = new RadioButton("General");

	        assignmentTag.setToggleGroup(tagGroup);
	        examTag.setToggleGroup(tagGroup);
	        generalTag.setToggleGroup(tagGroup);

	        Button submitButton = new Button("Submit");
	        Button backButton = new Button("Back");

	        submitButton.setOnAction(e -> {
	            String content = questionField.getText().trim();
	            RadioButton selectedTag = (RadioButton) tagGroup.getSelectedToggle();
	            
	            // **Input Validation**
	            if (content.isEmpty()) {
	                errorLabel.setText("Error: Question cannot be empty.");
	                return;
	            }
	            if (selectedTag == null) {
	                errorLabel.setText("Error: You must select a tag.");
	                return;
	            }

	            try {
	                User updatedUser = databaseHelper.getUserByUserName(user.getUserName());
	                if (updatedUser == null) {
	                    errorLabel.setText("Error: User not found.");
	                    return;
	                }

	                String tag = selectedTag.getText(); // Get the selected tag
	                Question question = new Question(0, content, updatedUser, tag);
	                databaseHelper.addQuestion(question);
	                show(primaryStage,user);
	            } catch (SQLException ex) {
	                ex.printStackTrace();
	            }
	        });

	        backButton.setOnAction(e -> show(primaryStage,user));

	        layout.getChildren().addAll(titleLabel, questionField, assignmentTag, examTag, generalTag, submitButton, errorLabel, backButton);
	        primaryStage.setScene(new Scene(layout, 800, 400));
	    }

	    // ****************************
	    // ** List Questions Page **
	    // ****************************
	    
	    private void showListQuestionsPage(Stage primaryStage) {
	        VBox layout = new VBox(15);
	        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

	        Label titleLabel = new Label("All Questions");
	        ScrollPane scrollPane = new ScrollPane();
	        VBox questionContainer = new VBox(10);

	        try {
	            List<Question> questions = sortQuestions.getSortedQuestions("date", false, "");

	            for (Question q : questions) {
	                VBox questionBox = new VBox(5);
	                String questionText = q.isResolved() ? "✅ " + q.getContent() : q.getContent(); 
	                Label questionLabel = new Label(questionText + " [" + q.getTags() + "]");
	                Button answerButton = new Button("Answer");

	                // Fetch answers for this question
	                VBox answersBox = new VBox(5);
	                List<Answer> answers = sortAnswers.getSortedAnswers(q.getId(), "date", user.getId());

	                for (Answer a : answers) {
	                    String answerText = a.isSolution() ? "✅ " + a.getContent() : a.getContent(); 
	                    Label answerLabel = new Label("→ " + answerText);

	                    if (a.isSolution()) {
	                        System.out.println("DEBUG: Marking answer as solution → " + a.getContent()); // Debug print
	                        answerLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold; -fx-padding: 5; -fx-background-color: #e8f5e9;"); // Green color for solutions
	                    } else {
	                        answerLabel.setStyle("-fx-padding: 5; -fx-background-color: #e8e8e8;");
	                    }

	                    answersBox.getChildren().add(answerLabel);
	                }

	                answerButton.setOnAction(e -> showAnswerForm(primaryStage, q, user));

	                questionBox.getChildren().addAll(questionLabel, answerButton, answersBox);
	                questionBox.setStyle("-fx-border-color: black; -fx-padding: 10; -fx-background-color: #f5f5f5;");
	                questionContainer.getChildren().add(questionBox);
	            }
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	        }

	        scrollPane.setContent(questionContainer);
	        scrollPane.setFitToWidth(true);

	        Button backButton = new Button("Back");
	        backButton.setOnAction(e -> show(primaryStage,user));

	        layout.getChildren().addAll(titleLabel, scrollPane, backButton);
	        primaryStage.setScene(new Scene(layout, 800, 400));
	    }

	    
	    private void showUserQuestionsPage(Stage primaryStage, User user) throws SQLException {
	        VBox layout = new VBox(15);
	        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	        VBox questionContainer = new VBox(10);
	        ScrollPane scrollPane = new ScrollPane(questionContainer);
	        scrollPane.setFitToWidth(true);

	        questionContainer.getChildren().clear();
	        List<Question> questions = databaseHelper.getQuestionsByUser(user.getId());

	        for (Question q : questions) {
	            VBox questionBox = new VBox(5);
	            questionBox.setStyle("-fx-border-color: black; -fx-padding: 10; -fx-background-color: #f5f5f5;");

	            // Question Label
	            Label questionLabel = new Label(q.toString());

	            // **Edit Button**
	            Button editButton = new Button("Edit");
	            TextField editField = new TextField();
	            editField.setVisible(false);
	            Button saveButton = new Button("Save");
	            saveButton.setVisible(false);

	            editButton.setOnAction(event -> {
	                editField.setText(q.getContent());
	                editField.setVisible(true);
	                saveButton.setVisible(true);
	            });

	            saveButton.setOnAction(event -> {
	                try {
	                    String updatedContent = editField.getText().trim();
	                    if (!updatedContent.isEmpty()) {
	                        databaseHelper.updateQuestion(q.getId(), updatedContent);
	                        questionLabel.setText(updatedContent);
	                        editField.setVisible(false);
	                        saveButton.setVisible(false);
	                    }
	                } catch (SQLException ex) {
	                    ex.printStackTrace();
	                }
	            });

	            // **Mark Resolved Button**
	            Button resolveButton = new Button(q.isResolved() ? "Resolved ✅" : "Mark as Resolved");
	            resolveButton.setOnAction(event -> {
	                try {
	                    databaseHelper.markQuestionResolved(q.getId());
	                    q.setResolved(true);
	                    questionLabel.setText(q.toString());
	                    resolveButton.setText("Resolved ✅");
	                    resolveButton.setDisable(true);
	                } catch (SQLException ex) {
	                    ex.printStackTrace();
	                }
	            });

	            // **Delete Question Button**
	            Button deleteButton = new Button("Delete");
	            deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
	            deleteButton.setOnAction(event -> {
	                try {
	                    databaseHelper.deleteQuestion(q.getId()); // Ensure only the question owner can delete
	                    questionContainer.getChildren().remove(questionBox);
	                    System.out.println("✅ Question deleted successfully.");
	                } catch (SQLException ex) {
	                    ex.printStackTrace();
	                }
	            });

	            questionBox.getChildren().addAll(questionLabel, editButton, editField, saveButton, resolveButton, deleteButton);

	            // Fetch and Display Answers
	            List<Answer> answers = databaseHelper.getAnswersByQuestionId(q.getId());
	            VBox answerContainer = new VBox(5);

	            if (answers.isEmpty()) {
	                answerContainer.getChildren().add(new Label("No answers yet."));
	            } else {
	                ToggleGroup toggleGroup = new ToggleGroup();
	                for (Answer ans : answers) {
	                    HBox answerBox = new HBox(10);
	                    Label answerLabel = new Label(ans.toString());

	                    // **Select Main Answer**
	                    RadioButton mainAnswerButton = new RadioButton("Mark as Main Answer");
	                    mainAnswerButton.setToggleGroup(toggleGroup);
	                    if (ans.isSolution()) {
	                        mainAnswerButton.setSelected(true);
	                    }

	                    mainAnswerButton.setOnAction(event -> {
	                        try {
	                            databaseHelper.markAnswerAsSolution(ans.getId());
	                            for (Answer otherAns : answers) {
	                                if (otherAns.getId() != ans.getId()) {
	                                    otherAns.setSolution(false);
	                                }
	                            }
	                            ans.setSolution(true);
	                            answerLabel.setText(ans.toString());
	                        } catch (SQLException ex) {
	                            ex.printStackTrace();
	                        }
	                    });

	                    answerBox.getChildren().addAll(answerLabel, mainAnswerButton);
	                    answerContainer.getChildren().add(answerBox);
	                }
	            }

	            questionBox.getChildren().add(answerContainer);
	            questionContainer.getChildren().add(questionBox);
	        }

	        Button backButton = new Button("Back");
	        backButton.setOnAction(e -> show(primaryStage, user));

	        layout.getChildren().addAll(scrollPane, backButton);
	        primaryStage.setScene(new Scene(layout, 800, 400));
	    }

            /**
	     * Displays the page for searching questions by tag.
	     *
	     * @param primaryStage the primary stage of the application
	     */
	       	    
	    private void showSearchByTagPage(Stage primaryStage) {
	        VBox layout = new VBox(15);
	        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

	        Label titleLabel = new Label("Search Questions by Tag:");
	        
	        // Tag buttons container
	        HBox tagButtonContainer = new HBox(10);
	        tagButtonContainer.setStyle("-fx-alignment: center;");

	        // Tag buttons
	        Button assignmentsButton = new Button("Assignments");
	        Button examsButton = new Button("Exams");
	        Button generalButton = new Button("General");

	        tagButtonContainer.getChildren().addAll(assignmentsButton, examsButton, generalButton);

	        // Question display area
	        VBox questionContainer = new VBox(10);
	        ScrollPane scrollPane = new ScrollPane(questionContainer);
	        scrollPane.setFitToWidth(true);

	        // Function to search and display questions
	        EventHandler<ActionEvent> searchHandler = event -> {
	            Button clickedButton = (Button) event.getSource();
	            String selectedTag = clickedButton.getText(); // Get tag from button text

	            try {
	                questionContainer.getChildren().clear(); // Clear previous results
	                List<Question> questions = databaseHelper.getQuestionsByTag(selectedTag);

	                for (Question q : questions) {
	                    VBox questionBox = new VBox(5);
	                    String askedBy = q.getUser().getUserName();
	                    String questionText = q.isResolved() ? "✅ " + q.getContent() : q.getContent();
	                    Label questionLabel = new Label(questionText + " [" + q.getTags() + "]" + " (asked by "+askedBy+")");

	                    Button answerButton = new Button("Answer");
			    //Button reviewButton = new Button("Review");
	                
	                    VBox answersBox = new VBox(5);
	                    List<Answer> answers = databaseHelper.getAnswersByQuestionId(q.getId());
	                    for (Answer a : answers) {
	                        String answerText = a.toString();
	                        Label answerLabel = new Label("→ " + answerText + " (answered by " +a.getUser().getUserName()+")");

	                        if (a.isSolution()) {
	                            answerLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold; -fx-padding: 5; -fx-background-color: #e8f5e9;");
	                        } else {
	                            answerLabel.setStyle("-fx-padding: 5; -fx-background-color: #e8e8e8;");
	                        }

	                        answersBox.getChildren().add(answerLabel);
	                    }

	                    answerButton.setOnAction(e -> showAnswerForm(primaryStage, q, user));
			    //reviewButton.setOnAction(e -> showReviewForm(primaryStage, q, user));
	                    Button reviewButton = new Button("Check Reviews");
	                    reviewButton.setOnAction(e -> showReviewForm(primaryStage, q, user));
	                    questionBox.getChildren().addAll(questionLabel, answerButton, reviewButton, answersBox);
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


	    // ****************************
	    // ** Show Reviews **
	    // ****************************

	/**
	     * Displays the review form for answers related to a specific question.
	     *
	     * @param primaryStage the primary stage of the application
	     * @param question     the question for which reviews are shown
	     * @param user         the current logged-in user
	     */
	
	   private void showReviewForm(Stage primaryStage, Question question, User user) {

	    	
	    	VBox layout = new VBox(15);
	        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

	        Label titleLabel = new Label("Reviews for Answers to: " + question.getContent());
	        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

	        VBox contentContainer = new VBox(10);
	        ScrollPane scrollPane = new ScrollPane(contentContainer);
	        scrollPane.setFitToWidth(true);

	        try {
	            List<Answer> answers = databaseHelper.getAnswersByQuestionId(question.getId());
	            Map<User, Integer> trustedMap = databaseHelper.getTrustedReviewersForStudent(user.getId());
	            Set<String> trustedReviewers = trustedMap.keySet().stream()
	                                                     .map(User::getUserName)
	                                                     .collect(Collectors.toSet());

	            if (answers.isEmpty()) {
	                contentContainer.getChildren().add(new Label("No answers available for this question."));
	            } else {
	                // Sort answers: those from trusted reviewers first
	                answers.sort((a1, a2) -> {
	                    boolean a1Trusted = trustedReviewers.contains(a1.getUser().getUserName());
	                    boolean a2Trusted = trustedReviewers.contains(a2.getUser().getUserName());
	                    return Boolean.compare(a2Trusted, a1Trusted); // Descending order (trusted first)
	                });

	                for (Answer answer : answers) {
	                    VBox answerBox = new VBox(5);
	                    answerBox.setStyle("-fx-padding: 10; -fx-border-color: #ccc; -fx-background-color: #f8f8f8;");

	                    Label answerLabel = new Label("Answer: " + answer.getContent() + " (by " + answer.getUser().getUserName() + ")");
	                    answerLabel.setStyle("-fx-font-weight: bold;");

	                    VBox reviewBox = new VBox(3);
	                    List<Review> reviews = databaseHelper.getReviewsByAnswerId(answer.getId());

	                    if (reviews.isEmpty()) {
	                        reviewBox.getChildren().add(new Label("No reviews yet."));
	                    } else {
	                        for (Review review : reviews) {
	                        	HBox reviewRow = new HBox(10);
	                            Label reviewLabel = new Label("- " + review.getReviewer().getUserName() + ": " + review.getContent());
	                            Button feedbackButton = new Button("Send Feedback");
	                            feedbackButton.setOnAction(e -> showFeedbackForm(primaryStage, user, review));

	                            reviewRow.getChildren().addAll(reviewLabel, feedbackButton);
	                            reviewBox.getChildren().add(reviewRow);
	                        }
	                    }

	                    answerBox.getChildren().addAll(answerLabel, reviewBox);
	                    contentContainer.getChildren().add(answerBox);
	                }
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	            contentContainer.getChildren().add(new Label("Error loading reviews."));
	        }
	        

	        Button backButton = new Button("Back");
	        backButton.setOnAction(e -> {
				showSearchByTagPage(primaryStage);
			});

	        layout.getChildren().addAll(titleLabel, scrollPane, backButton);
	        primaryStage.setScene(new Scene(layout, 800, 400));
		}

	    // ****************************
	    // ** Answer Question Page **
	    // ****************************
	  
	    private void showUserAnswersPage(Stage primaryStage, User user) throws SQLException {
	        VBox layout = new VBox(15);
	        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	        VBox answerContainer = new VBox(10);
	        ScrollPane scrollPane = new ScrollPane(answerContainer);
	        scrollPane.setFitToWidth(true);

	        answerContainer.getChildren().clear(); // Clear previous content
	        List<Answer> answers = databaseHelper.getAnswersByUser(user.getId());

	        if (answers.isEmpty()) {
	            answerContainer.getChildren().add(new Label("No answers found."));
	        }

	        for (Answer a : answers) {
	            VBox answerBox = new VBox(5);
	            Label questionLabel = new Label("Q: " + a.getQuestion().getContent());
	            Label answerLabel = new Label("A: " + a.getContent());

	            // **Edit Button**
	            Button editButton = new Button("Edit");
	            TextField editField = new TextField();
	            editField.setVisible(false);
	            Button saveButton = new Button("Save");
	            saveButton.setVisible(false);

	            editButton.setOnAction(event -> {
	                editField.setText(a.getContent());
	                editField.setVisible(true);
	                saveButton.setVisible(true);
	            });

	            saveButton.setOnAction(event -> {
	                try {
	                    String updatedContent = editField.getText().trim();
	                    if (!updatedContent.isEmpty()) {
	                        databaseHelper.updateAnswer(a.getId(), updatedContent);
	                        answerLabel.setText("A: " + updatedContent);
	                        editField.setVisible(false);
	                        saveButton.setVisible(false);
	                    }
	                } catch (SQLException ex) {
	                    ex.printStackTrace();
	                }
	            });

	            // **Delete Button**
	            Button deleteButton = new Button("Delete");
	            deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
	            deleteButton.setOnAction(event -> {
	                try {
	                    databaseHelper.deleteAnswer(a.getId(), user.getId()); // Ensure only the answer owner can delete
	                    answerContainer.getChildren().remove(answerBox);
	                    System.out.println("Answer deleted successfully.");
	                } catch (SQLException ex) {
	                    ex.printStackTrace();
	                }
	            });

	            answerBox.getChildren().addAll(questionLabel, answerLabel, editButton, editField, saveButton, deleteButton);
	            answerBox.setStyle("-fx-border-color: black; -fx-padding: 10; -fx-background-color: #e8e8e8;");
	            answerContainer.getChildren().add(answerBox);
	        }

	        Button backButton = new Button("Back");
	        backButton.setOnAction(e -> show(primaryStage, user));

	        layout.getChildren().addAll(scrollPane, backButton);
	        primaryStage.setScene(new Scene(layout, 800, 400));
	    }

             /**
	     * Displays the page for answering a question.
	     *
	     * @param primaryStage the primary stage of the application
	     * @param question     the question to be answered
	     * @param user         the current logged-in user
	     */
	    private void showAnswerForm(Stage primaryStage, Question question, User user) {
	        VBox layout = new VBox(15);
	        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

	        Label titleLabel = new Label("Answer the Question:");
	        Label questionLabel = new Label(question.toString());
	        TextField answerField = new TextField();
	        answerField.setPromptText("Enter your answer...");
	        Label Submitted = new Label();
	        Label errorLabel = new Label();
	        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

	        Button submitButton = new Button("Submit");
	        Button backButton = new Button("Back");
            
	        submitButton.setOnAction(e -> {
	            String content = answerField.getText().trim();
	            if (content.isEmpty()) {
	                errorLabel.setText("Error: Answer cannot be empty.");
	                return;
	            }
	            if (content.length() < 5) {
	                errorLabel.setText("Error: Answer must be at least 5 characters long.");
	                return;
	            }

	            try {
	                Answer answer = new Answer(0, content, user, question);
	                databaseHelper.addAnswer(answer);
	                //showListQuestionsPage(primaryStage);
	                Submitted.setText("Answer submitted!");
	            } catch (SQLException ex) {
	                ex.printStackTrace();
	            }
	        });

	        backButton.setOnAction(e -> showSearchByTagPage(primaryStage));

	        layout.getChildren().addAll(titleLabel, questionLabel, answerField, submitButton, Submitted, errorLabel, backButton);
	        primaryStage.setScene(new Scene(layout, 800, 400));
	    }

	//Search Page
            /**
	     * Displays the page for searching questions and reviewers.
	     *
	     * @param primaryStage the primary stage of the application
	     * @param user         the current logged-in user
	     */
	private void showSearchPage(Stage primaryStage, User user2) {
	    	VBox layout = new VBox(15);
	        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

	        Label searchLabel = new Label("Search Questions & Reviewers");
	        TextField searchField = new TextField();
	        searchField.setPromptText("Enter keyword");

	     // Tag buttons container
	        HBox searchButtonContainer = new HBox(10);
	        searchButtonContainer.setStyle("-fx-alignment: center;");

	        // Tag buttons
	        Button answeredButton = new Button("Answered Questions");
	        Button unansweredButton = new Button("Unanswered Questions");
	        Button reviewerButton = new Button("Reviewers");
	        Button backButton = new Button("Back");
	        
	        VBox resultBox = new VBox(10);
	        ScrollPane scrollPane = new ScrollPane(resultBox);
	        scrollPane.setFitToWidth(true);
	        
	        Label errorLabel = new Label();
	        errorLabel.setStyle("-fx-text-fill: red;");
	        
	        answeredButton.setOnAction(e -> {
	        	showAnsweredButton(resultBox, searchField, errorLabel, layout, 
	    	    		 primaryStage, user);
	        	});
	        
        	
	        
	        unansweredButton.setOnAction(e -> {
        	showUnansweredButton(resultBox, searchField, errorLabel, layout, 
    	    		 primaryStage, user);
	        	});
    	
	        
	        reviewerButton.setOnAction(e -> {
        	showReviewerButton(resultBox, searchField, errorLabel, layout, 
    	    		 primaryStage, user);
        	
        		});
	        		
	 
	        
	        
	       backButton.setOnAction(e -> show(primaryStage,user));
	        

	        searchButtonContainer.getChildren().addAll(answeredButton, unansweredButton, reviewerButton, backButton);
	        
	        layout.getChildren().addAll(searchButtonContainer, backButton);
	        primaryStage.setScene(new Scene(layout, 800, 400));
	        
	    }

	private void showReviewerButton(VBox resultBox, TextField searchField, Label errorLabel, VBox layout,
				Stage primaryStage, User user2) {
	    
		    	layout.getChildren().remove(resultBox);
		    	layout.getChildren().remove(searchField);
		    	layout.getChildren().remove(errorLabel);
		    	resultBox.getChildren().clear();
	            String keyword = searchField.getText().trim();
	            
	            if (keyword.isEmpty()) {
	            	
	                errorLabel.setText("Please enter a search keyword.");
	               
	            }
	           
	            errorLabel.setText("");
	            try {
	                List<String> reviewers = databaseHelper.getReviewers();
	                searchField.setOnAction(e -> {
	                showReviewers(searchField, reviewers, resultBox, layout, errorLabel, primaryStage, user);});
	                
	                if (reviewers.isEmpty()) {
	                    resultBox.getChildren().add(new Label("No answered questions found."));
	                } else {
	                    for (String s : reviewers) {
	                    	Label label = new Label(s);
	                    	resultBox.getChildren().add(label);
	                    	
	                    }
	                }
	            } catch (Exception ex) {
	                ex.printStackTrace();
	                resultBox.getChildren().add(new Label("Error fetching answered questions."));
	            //}
	            }
	            Button backButton = new Button("Back");
		        
		        backButton.setOnAction(e -> showSearchPage(primaryStage, user));
	            layout.getChildren().addAll(searchField, errorLabel, resultBox);
	            
		}

	   /**
	 * Filters and displays reviewers that match the entered search keyword.
	 *
	 * @param searchField   the input field for reviewer search
	 * @param reviewers     the complete list of reviewers
	 * @param resultBox     the box where matching reviewers are displayed
	 * @param layout        the main layout
	 * @param errorLabel    the label to display errors
	 * @param primaryStage  the primary stage of the application
	 * @param user2         the logged-in user
	 */
		private void showReviewers(TextField searchField, List<String> reviewers, VBox resultBox, VBox layout, Label errorLabel, Stage primaryStage, User user2) {
			layout.getChildren().remove(resultBox);
			layout.getChildren().remove(errorLabel);
			resultBox.getChildren().clear();
			String keyword = searchField.getText();
			
			if (keyword.isEmpty()) {
           	
                errorLabel.setText("Please enter a search keyword.");
               
            }
			for(String s : reviewers) {
				if(s.contains(keyword)) {
					Label sLabel = new Label(s);
                    resultBox.getChildren().add(sLabel);
					
				}
			}
			layout.getChildren().addAll(resultBox, errorLabel);
		}

		private void showUnansweredButton(VBox resultBox, TextField searchField, Label errorLabel, VBox layout,
		Stage primaryStage, User user) {
	    	
			layout.getChildren().remove(resultBox);
	    	layout.getChildren().remove(searchField);
	    	layout.getChildren().remove(errorLabel);
	    	resultBox.getChildren().clear();
            String keyword = searchField.getText().trim();
			
            if (keyword.isEmpty()) {
            	
                errorLabel.setText("Please enter a search keyword.");
               
            }
            errorLabel.setText("");
            try {
                List<Question> unansweredQuestions = databaseHelper.getQuestionsSortedByStatus();
                searchField.setOnAction(e -> {
                showUnansweredQuestions(searchField, unansweredQuestions, resultBox, layout, primaryStage, user);});
                
                if (unansweredQuestions.isEmpty()) {
                    resultBox.getChildren().add(new Label("No answered questions found."));
                } else {
                    for (Question q : unansweredQuestions) {
                    	if(!q.isResolved()) {
                        Label qLabel = new Label(q.getContent() + " [" + q.getTags() + "]");
                        resultBox.getChildren().add(qLabel);
                    	}
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                resultBox.getChildren().add(new Label("Error fetching answered questions."));
            }
            Button backButton = new Button("Back");
	        
	        backButton.setOnAction(e -> showSearchPage(primaryStage, user));
            layout.getChildren().addAll(searchField, errorLabel, resultBox);
            
		}

		private void showUnansweredQuestions(TextField searchField, List<Question> unansweredQuestions,
				VBox resultBox, VBox layout, Stage primaryStage, User user2) {
			
			layout.getChildren().remove(resultBox);
			resultBox.getChildren().clear();
			List<Question> unansweredQuestion = new ArrayList<>();
			for(Question q : unansweredQuestions) {
				if(q.getContent().contains(searchField.getText())) {
					Label qLabel = new Label(q.getContent() + " [" + q.getTags() + "]");
                    resultBox.getChildren().add(qLabel);
					
				}
			}
			layout.getChildren().add(resultBox);
		}

		private void showAnsweredButton(VBox resultBox, TextField searchField, Label errorLabel, VBox layout, 
	    		Stage primaryStage, User user) {
	    	layout.getChildren().remove(resultBox);
	    	layout.getChildren().remove(searchField);
	    	layout.getChildren().remove(errorLabel);
	    	
	    	resultBox.getChildren().clear();
            String keyword = searchField.getText().trim();
            
            
            
            if (keyword.isEmpty()) {
            	
                errorLabel.setText("Please enter a search keyword.");
               
            }
            errorLabel.setText(""); // Clear previous errors
            try {
                List<Question> answeredQuestions = databaseHelper.getQuestionsSortedByStatus();
                searchField.setOnAction(e -> {
                showAnsweredQuestions(searchField, answeredQuestions, resultBox, layout, primaryStage, user);});
                
                if (answeredQuestions.isEmpty()) {
                    resultBox.getChildren().add(new Label("No answered questions found."));
                } else {
                    for (Question q : answeredQuestions) {
                    	if(q.isResolved()) {
                        Label qLabel = new Label("✅ " + q.getContent() + " [" + q.getTags() + "]");
                        resultBox.getChildren().add(qLabel);
                    	}
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                resultBox.getChildren().add(new Label("Error fetching answered questions."));
            }
            Button backButton = new Button("Back");
	        
	        backButton.setOnAction(e -> showSearchPage(primaryStage, user));
            layout.getChildren().addAll(searchField, errorLabel, resultBox);
	       
            
	    }
		private void showAnsweredQuestions(TextField searchField, List<Question> answeredQuestions, VBox resultBox, 
				VBox layout, Stage primaryStage, User user) {
			layout.getChildren().remove(resultBox);
			resultBox.getChildren().clear();
			List<Question> answeredQuestion = new ArrayList<>();
			for(Question q : answeredQuestions) {
				if(q.getContent().contains(searchField.getText())) {
					Label qLabel = new Label("✅ " + q.getContent() + " [" + q.getTags() + "]");
                    resultBox.getChildren().add(qLabel);
					//answeredQuestion.add(q);
				}
			}
			
			
			layout.getChildren().add(resultBox);
			
		}

	        /**
	     * Removes a reviewer by moving the selected reviewer from the trusted reviewer list back to the reviewer list.
	     *
	     * @param myReviewerList the trusted reviewers list
	     * @param reviewerList   the available reviewers list
	     */
	
		public void removeReviewer(ComboBox<String> myReviewerList, ComboBox<String> reviewerList) {
			String selectedReviewer = myReviewerList.getValue();
			reviewerList.getItems().add(selectedReviewer);
			myReviewerList.getItems().remove(selectedReviewer);
			
		}

	    /**
	     * Adds a reviewer by moving the selected reviewer from the reviewer list to the trusted reviewer list.
	     *
	     * @param myReviewerList the trusted reviewers list
	     * @param reviewerList   the available reviewers list
	     */
		public void addReviewer(ComboBox<String> myReviewerList, ComboBox<String> reviewerList) {
			String selectedReviewer = reviewerList.getValue();
			myReviewerList.getItems().add(selectedReviewer);
			reviewerList.getItems().remove(selectedReviewer);
		}
		
		private void showFeedbackForm(Stage primaryStage, User student, Review review) {
		    Stage feedbackStage = new Stage();
		    VBox layout = new VBox(10);
		    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

		    Label titleLabel = new Label("Provide Feedback to " + review.getReviewer().getUserName());
		    TextArea feedbackArea = new TextArea();
		    feedbackArea.setPromptText("Enter your feedback here...");

		    Button submitButton = new Button("Submit");
		    submitButton.setOnAction(e -> {
		        String feedbackText = feedbackArea.getText().trim();
		        if (!feedbackText.isEmpty()) {
		            try {
		                databaseHelper.addFeedback(student.getId(), review.getReviewer().getId(), review.getAnswer().getId(), review.getId(), feedbackText);
		                feedbackStage.close();
		            } catch (SQLException ex) {
		                ex.printStackTrace();
		            }
		        }
		    });

		    layout.getChildren().addAll(titleLabel, feedbackArea, submitButton);
		    feedbackStage.setScene(new Scene(layout, 400, 250));
		    feedbackStage.show();
		}
}
