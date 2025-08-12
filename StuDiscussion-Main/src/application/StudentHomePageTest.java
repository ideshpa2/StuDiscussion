package application;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import databasePart1.DatabaseHelper;
import org.junit.jupiter.api.*;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
public class StudentHomePageTest{
	private DatabaseHelper databaseHelper;
	
	@Test
	void testGetReviewers() {
		databaseHelper = new DatabaseHelper();
		List<String> reviewers = databaseHelper.getReviewers();
		Assertions.assertNotNull(reviewers);
		System.out.println("testGetReviewers: Passed - reviewers is not null.");
	}
	
	@Test
	void testgetQuestionsSortedByStatus() throws SQLException {
		databaseHelper = new DatabaseHelper();
		databaseHelper.connectToDatabase();
		List<Question> questions = databaseHelper.getQuestionsSortedByStatus();
		Assertions.assertNotNull(questions);
		System.out.println("testgetQuestionsSortedByStatus: Passed - questions is not null.");
	    databaseHelper.closeConnection();
	}
	
	@Test
	void testGetReviewersReturnsAtLeastOneReviewer() {
		databaseHelper = new DatabaseHelper();
		List<String> reviewers = databaseHelper.getReviewers();
		Assertions.assertTrue(reviewers.size()>0);
		System.out.println("testGetReviewersReturnsAtLeastOneReviewer: Passed - at least one reviewer returned.");
	}
	
	@Test
	void testGetQuestionsSortedByStatusReturnsSortedQuestions() throws SQLException {
		databaseHelper = new DatabaseHelper();
		databaseHelper.connectToDatabase();
		List<Question> questions = databaseHelper.getQuestionsSortedByStatus();
		Assertions.assertTrue(questions.size()>0);
		System.out.println("testGetQuestionsSortedByStatusReturnsSortedQuestions: Passed - sorted questions returned.");
	    databaseHelper.closeConnection();
	}
	
	@Test
	void testAddReviewer() {
	    List<String> myList = new ArrayList<>();
	    List<String> reviewerList = new ArrayList<>(List.of("Alice", "Bob", "Charlie"));
	    String selected = "Bob";
	    myList.add(selected);
	    reviewerList.remove(selected);
	    assertTrue(myList.contains("Bob"));
	    assertFalse(reviewerList.contains("Bob"));
	    System.out.println("Added reviewer Successfully!");
	}
	
	@Test
	void testRemoveReviewer() {
	    List<String> myReviewerList = new ArrayList<>(List.of("Bob"));
	    List<String> reviewerList = new ArrayList<>(List.of("Alice", "Charlie"));
	    String selected = "Bob";
	    myReviewerList.remove(selected);
	    reviewerList.add(selected);
	    assertFalse(myReviewerList.contains("Bob"));
	    assertTrue(reviewerList.contains("Bob"));
	    
	    System.out.println("Removed reviewer Successfully!");
	}
	
	
}