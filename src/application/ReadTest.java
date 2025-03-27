package application;

import java.sql.SQLException;

import databasePart1.DatabaseHelper;

public class ReadTest {
	
	
	    private static final DatabaseHelper databaseHelper = new DatabaseHelper();

	    public static void main(String[] args) throws SQLException {

	        System.out.println("______________________________________");
	        System.out.println("\nTesting Automation - User Retrieval");

	        
	        databaseHelper.connectToDatabase();
	        testgetQuestionById();
	        testgetQuestionById();
	    }
	    static void testgetQuestionById() throws SQLException {
	    	System.out.println("Read Question");
	       
	        databaseHelper.getQuestionById(1);
	       
	        System.out.println("Question successfully read!");
	        
	    }
	    
	    static void testgetAnswerById() throws SQLException {
	    	System.out.println("Read answer");
	       
	        databaseHelper. getAnswerById(1);
	       
	        System.out.println("answer successfully read!");
	        
	    }
	}

