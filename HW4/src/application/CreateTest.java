package application;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import databasePart1.DatabaseHelper;

public class CreateTest {
	
	
	    private static final DatabaseHelper databaseHelper = new DatabaseHelper();

	    public static void main(String[] args) throws SQLException {

	        System.out.println("______________________________________");
	        System.out.println("\nTesting Automation - User Retrieval");

	        /************** Start of the test cases **************/
	        databaseHelper.connectToDatabase();
	      
	       //testAddUser();
	        testAddQuestion();
	        testAddAnswer();
	        
	    }
	   
	    static void testAddAnswer() throws SQLException {
	    	System.out.println("Add answer");
	        List<String> userRole = new ArrayList<>(); 
	        userRole.add("student");
	        User user = new User(1, "Jane", "123456", "janeabc@asu.edu", userRole);
	        Question question = new Question(1, "How are you?", user, "tag1");
	        Answer answer = new Answer(1, "I am, good", user, question);
	        databaseHelper.addAnswer(answer);
	       
	        System.out.println("Answer successfully added!");
	        
	    }
	    
	    static void testAddQuestion() throws SQLException {
	    	System.out.println("Add question");
	        List<String> userRole = new ArrayList<>(); 
	        userRole.add("student");
	        User user = new User(1, "Jane", "123456", "janeabc@asu.edu", userRole);
	        Question question = new Question(1, "How are you?", user, "tag1");
	        databaseHelper.addQuestion(question);

	       
	        System.out.println("Question successfully added!");
	        
	    }
	    
//	   static void testAddUser() throws SQLException {
//	    	System.out.println("Add user");
//	        List<String> userRole = new ArrayList<>(); 
//	        userRole.add("student");
//	        User user = new User(1, "Jane", "123456", "janeabc@asu.edu", userRole);
//	        databaseHelper.register(user, userRole);
//
//	       
//	        System.out.println("User is created");
//	        
//	    }
	}

