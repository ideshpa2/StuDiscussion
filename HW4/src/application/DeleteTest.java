package application;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import databasePart1.DatabaseHelper;

public class DeleteTest {
	
	
	    private static final DatabaseHelper databaseHelper = new DatabaseHelper();

	    public static void main(String[] args) throws SQLException {

	        System.out.println("______________________________________");
	        System.out.println("\nTesting Automation - User Retrieval");

	        /************** Start of the test cases **************/
	        databaseHelper.connectToDatabase();
	      
	       
	        
	        testDeleteAnswer();
	        testDeleteQuestion();
	        
	    }
	   
	    static void testDeleteAnswer() throws SQLException {
	    	System.out.println("Delete answer");
	       
	        databaseHelper.deleteAnswer(1,1);
	       
	        System.out.println("Answer successfully deleted!");
	        
	    }
	    
	    static void testDeleteQuestion() throws SQLException {
	    	System.out.println("Delete question");
	       
	        databaseHelper.deleteQuestion(1);
	       
	        System.out.println("Question successfully deleted!");
	        
	    }
	    
	   
	}

