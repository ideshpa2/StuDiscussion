package application;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * InstructorHomePage class represents the user interface for the instructor user.
 * This page displays a welcome message for the instructor and provides options
 * to review users' questions and answers, switch roles, and log out.
 */
public class InstructorHomePage {
	
	
	private DatabaseHelper databaseHelper;
	 /**
     * Constructs an InstructorHomePage instance.
     * 
     * @param databaseHelper The DatabaseHelper instance for database interactions.
     */
	public InstructorHomePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
        //this.user = user;
    }
	/**
     * Displays the instructor page in the provided primary stage.
     * 
     * @param primaryStage The primary stage where the scene will be displayed.
     * @param user The logged-in user.
     */
    public void show(Stage primaryStage, User user) 
    {
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
        /** 
         * drop down to select user to get questions and answers
         */
        ComboBox<String> userDropdown = new ComboBox<>();
        ArrayList<String> users = databaseHelper.getAllUsernames();
        for(String userName : users)
        {
        	try{
        		User u = databaseHelper.getUserByUserName(userName);
        		if(u.getRoles().contains("Student"))
        		{
        			userDropdown.getItems().add(userName);
        		}
        	}catch(Exception e)
        	{
        		e.printStackTrace();
        		System.out.println(e.getMessage());
        	}
        }
        
        Button selectUser = new Button("Please select Student to review their question's and answers");
        
        
        
        selectUser.setOnAction(e -> 
        {
        	String userName = userDropdown.getValue();
        	try
            {
            	//System.out.println("Trying to get User object for " + userName);
            	User u = databaseHelper.getUserByUserName(userName);
            	if(u == null)
            	{
            		System.out.println("U object is null");
            	}else if(user == null)
            	{
            		System.out.println("User object is null");
            	}
            	
            	showQuestionsAndAnswerPageByUser(primaryStage, u ,user);
            }catch(Exception ex)
            {
            	return;
            }
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
        Label students = new Label();
        
        Button getReviewerRequests = new Button("Click here to view all students requesting to be reviewers");
        getReviewerRequests.setOnAction(e-> {
        	try{
        		List<String> studentsList = databaseHelper.getStudentsRequestingReviewerRole();
        		String studentString = "";
        		for(String student : studentsList)
        		{
        			studentString = studentString + student + "\n";
        		}
        		if(studentString == "")
        		{
        			students.setText("There is no students requesting to be a reviewer at this time\n");
        		}else {
        			students.setText(studentString);
        		}
        	}catch(Exception ex)
        	{
        		System.out.println(ex.getMessage());
        	}
        	
        });
        
        
	    layout.getChildren().addAll(instructorLabel,userDropdown, selectUser, logoutButton, roleDropdown, switchRole, getReviewerRequests, students);
	    Scene instructorScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(instructorScene);
	    primaryStage.setTitle("instructor Page");
    
	    
    }
    /**
     * Displays the questions and answers of a selected user.
     * 
     * @param primaryStage The primary stage where the scene will be displayed.
     * @param u The user whose questions and answers will be displayed.
     * @param instructor The logged-in instructor.
     */
	    private void showQuestionsAndAnswerPageByUser(Stage primaryStage, User u, User instructor) 
	    {
	        VBox layout = new VBox(20);
	        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	
	        Label titleLabel = new Label("Users Questions List");
	        ScrollPane scrollPane = new ScrollPane();
	        VBox questionContainer = new VBox(10);
	        int i = 1;
	        try {
	        	//System.out.println("About to retrieve questions");
	            List<Question> questions = databaseHelper.getQuestionsByUser(u.getId());
	            //System.out.println("Retrieved the questions");
	            
	            VBox questionBox = new VBox(10);
	            //int size = questions.size();
	            //System.out.println("questions list is size " + size);
	            for (Question q : questions) 
	            {
	                String questionText = q.isResolved() ? "✅ " + q.getContent() : q.getContent(); 
	                //System.out.println(questionText);
	                Label questionLabel = new Label(i + ": " + questionText + " [" + q.getTags() + "]");
	                i++;
	                questionBox.getChildren().addAll(questionLabel);
	                questionBox.setStyle("-fx-border-color: black; -fx-padding: 10; -fx-background-color: #f5f5f5;");
	                //questionContainer.getChildren().add(questionBox);
	            }
	            questionContainer.getChildren().add(questionBox);
	         } catch (Exception ex) {
	             System.out.println("ERROR processing question at index " + i + ": " + ex.getMessage());
	             ex.printStackTrace();
	         }
	                
	        
	
	                // Fetch answers for this question
	        Label titleLabelAnswers = new Label("Users Answers List");
	        ScrollPane scrollPaneAnswers = new ScrollPane();
	        VBox answerContainer = new VBox(10);
	        int j = 1;
	          try{
	        	  List<Answer> answers = databaseHelper.getAnswersByUser(u.getId());
	        	  VBox answerBox = new VBox(10);
	          for (Answer a : answers) {
	              String answerText = a.isSolution() ? "✅ " + a.getContent() : a.getContent();
	              String questionText = a.getQuestion().getContent();
	              Label answerLabel = new Label(j + ". Question = " + questionText + "\n	Answer -> " + answerText);
	              j++;
	              answerBox.getChildren().addAll(answerLabel);
	              answerBox.setStyle("-fx-border-color: black; -fx-padding: 10; -fx-background-color: #f5f5f5;");
	              
	              }
	          answerContainer.getChildren().add(answerBox);	
	        } catch (SQLException ex) {
	        	System.out.println("ERROR processing question at index " + j + ": " + ex.getMessage());
	            ex.printStackTrace();
	        }
	          
	        scrollPane.setContent(questionContainer);
	        scrollPane.setFitToWidth(true);  
	        scrollPaneAnswers.setContent(answerContainer);
	        scrollPaneAnswers.setFitToWidth(true);
	        Label resultLabel = new Label();
	        
	        Button makeReviewerButton = new Button("Click here to make the student a reviewer");
	        makeReviewerButton.setOnAction(e->
	        {
	        	try {
	                // Retrieve existing roles
	                ArrayList<String> roles = databaseHelper.getUserRoles(u.getUserName());

	                // Add the new role if not already assigned
	                if (!roles.contains("Reviewer")) {
	                    databaseHelper.addUserRole(u.getUserName(), "Reviewer"); // Use SQL method
	                    resultLabel.setText(u.getUserName() + " is now a Reviewer!");
	                } else {
	                    resultLabel.setText(u.getUserName() + " already has the reviewer role.");
	                }
	                if(databaseHelper.requestedReviewer(u))
	                {
	                	databaseHelper.removeReviewerRequest(u);
	                }
	            } catch (SQLException ex) {
	                resultLabel.setText("Error assigning role.");
	                ex.printStackTrace();
	            }
	        });
	        Button backButton = new Button("Back");
	        backButton.setOnAction(e -> show(primaryStage,instructor));
	
	        layout.getChildren().addAll(titleLabel, scrollPane, titleLabelAnswers, scrollPaneAnswers, makeReviewerButton, resultLabel, backButton);
	        primaryStage.setScene(new Scene(layout, 800, 400));
	    }
    }
