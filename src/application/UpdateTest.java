package application;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import databasePart1.DatabaseHelper;

public class UpdateTest {
	
	
	    private static final DatabaseHelper databaseHelper = new DatabaseHelper();

	    public static void main(String[] args) throws SQLException {

	        System.out.println("______________________________________");
	        System.out.println("\nTesting Automation - User Retrieval");

	        /************** Start of the test cases **************/
	        databaseHelper.connectToDatabase();
	      
	        testUpdateAnswer();
	        
	        testUpdateQuestion();
	        
	    }
	   
	    static void testUpdateAnswer() throws SQLException {
	    	System.out.println("Update answer");
	       
	        databaseHelper.updateAnswer(1,"I'm fine!");
	       
	        System.out.println("Answer successfully updated!");
	        
	    }
	    
	    static void testUpdateQuestion() throws SQLException {
	    	System.out.println("Update question");
	       
	        databaseHelper.updateQuestion(1, "How are you doing?");
	       
	        System.out.println("Question successfully updated!");
	        
	    }
	    
	   
	}

