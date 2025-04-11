package application;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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

public StudentHomePage(DatabaseHelper databaseHelper) {
    this.databaseHelper = databaseHelper;
    //this.user = user;
    this.sortQuestions = new SortQuestions(databaseHelper);
    this.sortAnswers = new SortAnswers(databaseHelper);
}
	
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

        
	    layout.getChildren().addAll(studentLabel, askQuestionButton, viewMyQuestionsButton, viewMyAnswersButton, searchByTagButton, logoutButton, roleDropdown, switchRole);
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

	                    questionBox.getChildren().addAll(questionLabel, answerButton, answersBox);
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
}