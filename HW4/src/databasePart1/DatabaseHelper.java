package databasePart1;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import application.Answer;
import application.Feedback;
import application.Question;
import application.Review;
import application.ReviewUpdate;
import application.User;

/**
 * The DatabaseHelper class is responsible for managing the connection to the database,
 * performing operations such as user registration, login validation, and handling invitation codes.
 */

public class DatabaseHelper {
	public static int num_users = 0;
	public int get_num_users() {return num_users;}
	public void increment_num_users() { num_users++;}

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	public Connection connection = null;
	private Statement statement = null; 
	//	PreparedStatement pstmt

	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			// You can use this command to clear the database and restart from fresh.
			// statement.execute("DROP ALL OBJECTS");

			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}
	
	public Connection getConnection() {
	    try {
	        if (connection == null || connection.isClosed()) {
	            connectToDatabase(); // Reconnect if closed
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return connection;
	}

	private void createTables() throws SQLException {
	    String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "userName VARCHAR(255) UNIQUE, "
	            + "password VARCHAR(255), "
	            + "email VARCHAR(255), "
	            + "role TEXT, "
	            + "one_time_password VARCHAR(255))"; // Store one-time passwords

	    statement.execute(userTable);
	    
	 // Create Questions table (AFTER Users)
	    String questionsTable = "CREATE TABLE IF NOT EXISTS Questions ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "content TEXT NOT NULL, "
	            + "user_id INT NOT NULL, "
	            + "tags VARCHAR(255), " // Store as a single string
	            + "date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
	            + "is_resolved BOOLEAN DEFAULT FALSE, "
	            + "FOREIGN KEY (user_id) REFERENCES cse360users(id))";
	    statement.execute(questionsTable);
	    
	    // Create Answers table (AFTER Questions)
	    String answersTable = "CREATE TABLE IF NOT EXISTS Answers ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "content TEXT NOT NULL, "
	            + "question_id INT NOT NULL, "
	            + "user_id INT NOT NULL, "
	            + "date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
	            + "is_solution BOOLEAN DEFAULT FALSE, "
	            + "FOREIGN KEY (question_id) REFERENCES Questions(id) ON DELETE CASCADE, "
	            + "FOREIGN KEY (user_id) REFERENCES cse360users(id) ON DELETE CASCADE)";
	    statement.execute(answersTable);
	    
	    // Create reviewsTable
	    String reviewsTable = "CREATE TABLE IF NOT EXISTS Reviews ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "content TEXT NOT NULL, "
	            + "reviewer_id INT NOT NULL, "
	            + "answer_id INT NOT NULL, "
	            + "updated_from_review_id INT, "
	            + "date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
	            + "FOREIGN KEY (reviewer_id) REFERENCES cse360users(id) ON DELETE CASCADE, "
	            + "FOREIGN KEY (answer_id) REFERENCES Answers(id) ON DELETE CASCADE, "
	            + "FOREIGN KEY (updated_from_review_id) REFERENCES Reviews(id))";
	    statement.execute(reviewsTable);
	    //Trusted reviewers
	    String trustedReviewersTable = "CREATE TABLE IF NOT EXISTS trustedreviewers ("
	    		+ "    student_id INT, "
	    		+ "    reviewer_id INT, "
	    		+ "    weight INT DEFAULT 1, "
	    		+ "    PRIMARY KEY (student_id, reviewer_id)"
	    		+ ")";
        statement.execute(trustedReviewersTable);

	    // Create ReadAnswers table (AFTER Answers)
	    String readAnswersTable = "CREATE TABLE IF NOT EXISTS ReadAnswers ("
	            + "user_id INT NOT NULL, "
	            + "answer_id INT NOT NULL, "
	            + "FOREIGN KEY (user_id) REFERENCES cse360users(id) ON DELETE CASCADE, "
	            + "FOREIGN KEY (answer_id) REFERENCES Answers(id) ON DELETE CASCADE, "
	            + "PRIMARY KEY (user_id, answer_id))";
	    statement.execute(readAnswersTable);

	    String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
	            + "code VARCHAR(10) PRIMARY KEY, "
	            + "isUsed BOOLEAN DEFAULT FALSE)";
	    statement.execute(invitationCodesTable);

	    String studentsRequestingReviewerRole = "CREATE TABLE IF NOT EXISTS studentsRequestReviewerRole ("
	    	    + "userName VARCHAR(255) UNIQUE, "
	    	    + "FOREIGN KEY (userName) REFERENCES cse360users(userName)"
	    	    + ")";
	    statement.execute(studentsRequestingReviewerRole);
	    
	    String reviewUpdates = "CREATE TABLE IF NOT EXISTS ReviewUpdates (\n"
	    		+ "    id INT AUTO_INCREMENT PRIMARY KEY,\n"
	    		+ "    review_id INT NOT NULL,\n"
	    		+ "    reviewer_id INT NOT NULL,\n"
	    		+ "    student_id INT NOT NULL,\n"
	    		+ "    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n"
	    		+ "    viewed BOOLEAN DEFAULT FALSE,\n"
	    		+ "    FOREIGN KEY (reviewer_id) REFERENCES cse360users(id) ON DELETE CASCADE,\n"
	    		+ "    FOREIGN KEY (student_id) REFERENCES cse360users(id) ON DELETE CASCADE,\n"
	    		+ "    FOREIGN KEY (review_id) REFERENCES Reviews(id) ON DELETE CASCADE\n"
	    		+ ")";
	    statement.execute(reviewUpdates);
	    
	    String ReviewFeedback = "CREATE TABLE IF NOT EXISTS ReviewFeedback (\n"
	    		+ "    id INT AUTO_INCREMENT PRIMARY KEY,\n"
	    		+ "    student_id INT NOT NULL,\n"
	    		+ "    reviewer_id INT NOT NULL,\n"
	    		+ "    answer_id INT NOT NULL,\n"
	    		+ "    review_id INT NOT NULL,\n"
	    		+ "    feedback TEXT NOT NULL,\n"
	    		+ "    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n"
	    		+ "    FOREIGN KEY (student_id) REFERENCES cse360users(id) ON DELETE CASCADE,\n"
	    		+ "    FOREIGN KEY (reviewer_id) REFERENCES cse360users(id) ON DELETE CASCADE,\n"
	    		+ "    FOREIGN KEY (answer_id) REFERENCES Answers(id) ON DELETE CASCADE,\n"
	    		+ "    FOREIGN KEY (review_id) REFERENCES Reviews(id) ON DELETE CASCADE\n"
	    		+ ")";
	    statement.execute(ReviewFeedback);
	    
	    String messagesTable = "CREATE TABLE IF NOT EXISTS Messages ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "sender_id INT NOT NULL, "
	            + "recipient_id INT NOT NULL, "
	            + "content TEXT NOT NULL, "
	            + "date_sent TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
	            + "FOREIGN KEY (sender_id) REFERENCES cse360users(id) ON DELETE CASCADE, "
	            + "FOREIGN KEY (recipient_id) REFERENCES cse360users(id) ON DELETE CASCADE)";
	    statement.execute(messagesTable);
	    
	    String probationReviewersTable = "CREATE TABLE IF NOT EXISTS ProbationReviewers ("
	            + "user_id INT PRIMARY KEY, "
	            + "date_added TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
	            + "FOREIGN KEY (user_id) REFERENCES cse360users(id) ON DELETE CASCADE)";
	    statement.execute(probationReviewersTable);

	}


	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}

	// Registers a new user in the database.
	public void register(User user, List<String> roles) throws SQLException {
	    String insertUser = "INSERT INTO cse360users (userName, password, email, role) VALUES (?, ?, ?, ?)";

	    try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
	        pstmt.setString(1, user.getUserName());
	        pstmt.setString(2, user.getPassword());
	        pstmt.setString(3, user.getEmail());
	        pstmt.setString(4, (roles == null || roles.isEmpty()) ? "" : String.join(",", roles)); // Fix for null/empty roles
	        pstmt.executeUpdate();
	    }
	}
	
	public User getUserById(int id) throws SQLException {
	    String query = "SELECT * FROM cse360users WHERE id = ?";
	 
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, id);
	        ResultSet rs = pstmt.executeQuery();

	        if (rs.next()) 
	        {
	            // Create the user object with basic info
	            String userName = rs.getString("userName");
	            String password = rs.getString("password");
	            String email = rs.getString("email");
	            
	            String rolesStr = rs.getString("role");
	            
		        List<String> roles = new ArrayList<>();
	            if(rolesStr != null && !rolesStr.isEmpty())
	            {
	            	String[] rolesArray = rolesStr.split(",");
	            	for(String role : rolesArray)
	            	{
	            		roles.add(role.trim());
	            	}
	            }
		        
	            return new User(id, userName, password, email, roles);
	        }

	    }
	    return null;
	}
	public User getUserByUserName(String userName) throws SQLException {
	    String query = "SELECT * FROM cse360users WHERE userName = ?";
	  
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) 
	    {
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) 
	        {
	            // Create the user object with basic info
	            int id = rs.getInt("id");
	            String password = rs.getString("password");
	            String email = rs.getString("email");
	            String rolesStr = rs.getString("role");
	            
		        List<String> roles = new ArrayList<>();
	            if(rolesStr != null && !rolesStr.isEmpty())
	            {
	            	String[] rolesArray = rolesStr.split(",");
	            	for(String role : rolesArray)
	            	{
	            		roles.add(role.trim());
	            	}
	            }
		        
	            return new User(id, userName, password, email, roles);
	        }

	    }
	    return null;
	}
	// Validates a user's login credentials.
	public boolean login(User user) throws SQLException {
	    String query = "SELECT * FROM cse360users WHERE userName = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, user.getUserName());

	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                String storedPassword = rs.getString("password");
	                String storedOtp = rs.getString("one_time_password");

	                // Check if entered password matches stored password or one-time password
	                if (user.getPassword().equals(storedPassword)) {
	                    return true; // Normal login
	                } else if (storedOtp != null && user.getPassword().equals(storedOtp)) {
	                    clearOneTimePassword(user.getUserName()); // Clear OTP after first use
	                    return true; // Login via OTP
	                }
	            }
	        }
	    }
	    return false; // Login failed
	}

	
	public void updateUserEmail(String userName, String email) throws SQLException {
	    String query = "UPDATE cse360users SET email = ? WHERE userName = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, email);
	        pstmt.setString(2, userName);
	        int rowsUpdated = pstmt.executeUpdate();

	        if (rowsUpdated == 0) {
	            throw new SQLException("No user found with the provided username.");
	        }
	    }
	}
	
	// Checks if a user already exists in the database based on their userName.
	public boolean doesUserExist(String userName) {
	    String query = "SELECT COUNT(*) FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}
	
	public boolean isOtpLogin(String userName, String password) throws SQLException {
	    String query = "SELECT one_time_password FROM cse360users WHERE userName = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                String storedOtp = rs.getString("one_time_password");
	                return storedOtp != null && storedOtp.equals(password);
	            }
	        }
	    }
	    return false;
	}

	
	public void updateUserRoles(String userName, ArrayList<String> newRoles) throws SQLException {
	    // Convert list to comma-separated string
	    String rolesString = String.join(",", newRoles);

	    String query = "UPDATE cse360users SET role = ? WHERE userName = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, rolesString);
	        pstmt.setString(2, userName);
	        int rowsUpdated = pstmt.executeUpdate();

	        if (rowsUpdated == 0) {
	            throw new SQLException("No user found with the provided username.");
	        }
	    }
	}
	
	public void updateUserPassword(String userName, String newPassword) throws SQLException {
	    String query = "UPDATE cse360users SET password = ?, one_time_password = NULL WHERE userName = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, newPassword);
	        pstmt.setString(2, userName);
	        pstmt.executeUpdate();
	    }
	}

	
	public void addUserRole(String userName, String newRole) throws SQLException {
	    ArrayList<String> roles = getUserRoles(userName);

	    if (!roles.contains(newRole)) {
	        roles.add(newRole);
	        updateUserRoles(userName, roles);
	    }
	}
	
	public void removeUserRole(String userName, String roleToRemove) throws SQLException {
	    ArrayList<String> roles = getUserRoles(userName);

	    if (!roles.contains(roleToRemove)) {
	        throw new SQLException("User does not have the specified role.");
	    }

	    // Prevent removing "Admin" role if it's the only admin in the system
	    if (roleToRemove.equals("Admin")) {
	        String checkAdminQuery = "SELECT COUNT(*) FROM cse360users WHERE role LIKE '%Admin%'";
	        try (PreparedStatement checkStmt = connection.prepareStatement(checkAdminQuery);
	             ResultSet rs = checkStmt.executeQuery()) {
	            if (rs.next() && rs.getInt(1) == 1) {
	                throw new SQLException("Cannot remove the last Admin role! There must always be at least one admin.");
	            }
	        }
	    }

	    // Remove the role
	    roles.remove(roleToRemove);
	    updateUserRoles(userName, roles);
	}

	
		
	// Retrieves all recorded userNames
	public ArrayList<String> getAllUsernames() {
	    ArrayList<String> usernames = new ArrayList<>();
	    String query = "SELECT userName FROM cse360users";

	    try (PreparedStatement pstmt = connection.prepareStatement(query);
	         ResultSet rs = pstmt.executeQuery()) {

	        while (rs.next()) {
	            usernames.add(rs.getString("userName"));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return usernames;
	}


	
	// Retrieves the role of a user from the database using their UserName.
	public ArrayList<String> getUserRoles(String userName) {
	    String query = "SELECT role FROM cse360users WHERE userName = ?";
	    ArrayList<String> roles = new ArrayList<>();

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();

	        if (rs.next()) {
	            String roleString = rs.getString("role");
	            if (roleString != null && !roleString.trim().isEmpty()) {
	                String[] roleArray = roleString.split(",");
	                for (String role : roleArray) {
	                    roles.add(role.trim());
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return roles;
	}

	
	// Retrieves userName, password, and role in list form
	public ArrayList<String> getAllUsers() {
	    ArrayList<String> users = new ArrayList<>();
	    String query = "SELECT userName, password, email, role FROM cse360users"; // Added email field

	    try {
	        if (connection == null || connection.isClosed()) { // Ensure connection is initialized
	            connectToDatabase();
	        }

	        try (PreparedStatement pstmt = connection.prepareStatement(query);
	             ResultSet rs = pstmt.executeQuery()) {

	            while (rs.next()) {
	                String userName = rs.getString("userName");
	                String password = rs.getString("password");
	                String email = rs.getString("email");  // Fetching email
	                String role = rs.getString("role");

	                // Format user details into a single string
	                String userDetails = "Username: " + userName + 
	                                     " | Password: " + password + 
	                                     " | Email: " + email + 
	                                     " | Role: " + role;
	                users.add(userDetails);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return users;
	}
	
	// Generates a new invitation code and inserts it into the database.
	public String generateInvitationCode() {
	    String code = UUID.randomUUID().toString().substring(0, 4); // Generate a random 4-character code
	    String query = "INSERT INTO InvitationCodes (code) VALUES (?)";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return code;
	}
	
	// Validates an invitation code to check if it is unused.
	public boolean validateInvitationCode(String code) {
	    String query = "SELECT * FROM InvitationCodes WHERE code = ? AND isUsed = FALSE";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            // Mark the code as used
	            markInvitationCodeAsUsed(code);
	            return true;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	public void setOneTimePassword(String userName, String otp) throws SQLException {
	    String query = "UPDATE cse360users SET one_time_password = ? WHERE userName = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, otp);
	        pstmt.setString(2, userName);
	        int rowsUpdated = pstmt.executeUpdate();

	        if (rowsUpdated == 0) {
	            throw new SQLException("User not found or OTP could not be set.");
	        }
	    }
	}
	
	public String getOneTimePassword(String userName) throws SQLException {
	    String query = "SELECT one_time_password FROM cse360users WHERE userName = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                return rs.getString("one_time_password"); // Return the OTP from the database
	            } else {
	                throw new SQLException("User not found.");
	            }
	        }
	    }
	}
	
	public void clearOneTimePassword(String userName) throws SQLException {
	    String query = "UPDATE cse360users SET one_time_password = NULL WHERE userName = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        pstmt.executeUpdate();
	    }
	}

	
	// Marks the invitation code as used in the database.
	private void markInvitationCodeAsUsed(String code) {
	    String query = "UPDATE InvitationCodes SET isUsed = TRUE WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	/////////////deleteUser() added by jace if any issues let me know////////////
	public String deleteUser(String username)
	{        
		if(!doesUserExist(username))
		{
			System.out.println("Username: " + username + " not associated with any known account.");
			return "Username: " + username + " not associated with any known account.";
		}
		ArrayList<String> roles = getUserRoles(username);
		if(roles.contains("admin"))
		{
			System.out.println("Admins cannot delete their own account.");
			return "Admins cannot delete their own account.";
		}
		
		
		int userID = -1;
		
		String query = "SELECT id FROM cse360users WHERE userName = ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(query))
		{
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next())
			{
				userID = rs.getInt("id");
			} else {
				System.out.println("User not found");
				throw new SQLException("User not found");
			}
		}catch(SQLException e) {
			e.printStackTrace();
			System.out.println("ERROR");
			return "ERROR";
		}
	
		String deleteQuery = "DELETE FROM cse360users WHERE id = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(deleteQuery))
		{
			pstmt.setInt(1, userID);
			int changed = pstmt.executeUpdate();
			
			if(changed > 0)
			{
				System.out.println("User: " + username + " succesfully deleted from the database");
				return "";
			}else {
				System.out.println("Failed to delete user");
				return "Failed to delete user.";
			}
		}catch (SQLException e )
		{
			e.printStackTrace();
			System.out.println("ERROR");
			return "ERROR";
		}
	}
	/////////////////////////////////////////////////////////////////////////////
	
	
	
	// Closes the database connection and statement.
	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}
	
	
	// ************ QUESTIONS ************ // 
	
		// Create question
		
		public void addQuestion(Question question) throws SQLException {
		    if (question.getUser().getId() == 0) {
		        throw new SQLException("Invalid user ID. Ensure user exists in database before adding a question.");
		    }
		    
		    String query = "INSERT INTO Questions (content, user_id, tags) VALUES (?, ?, ?)";
		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setString(1, question.getContent());
		        pstmt.setInt(2, question.getUser().getId());
		        pstmt.setString(3, String.join(",", question.getTags()));
		        pstmt.executeUpdate();
		    }
		}
		
		// Retrieve question by ID
		
		public Question getQuestionById(int questionId) throws SQLException {
		    String query = "SELECT * FROM Questions WHERE id = ?";
		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setInt(1, questionId);
		        ResultSet rs = pstmt.executeQuery();

		        if (rs.next()) {
		            User user = getUserById(rs.getInt("user_id"));
		            String tags = rs.getString("tags");
		            boolean isResolved = rs.getBoolean("is_resolved");

		            Question question = new Question(rs.getInt("id"), rs.getString("content"), user, tags);
		            question.setResolved(isResolved);  // ✅ Set the resolution status
		            return question;
		        }
		    }
		    return null;
		}

		public List<Question> getQuestionsByUser(int userId) throws SQLException {
		    List<Question> questions = new ArrayList<>();
		    String query = "SELECT id, content, user_id, tags, is_resolved FROM Questions WHERE user_id = ?";

		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setInt(1, userId);
		        ResultSet rs = pstmt.executeQuery();

		        while (rs.next()) {
		            User user = getUserById(rs.getInt("user_id"));
		            String tags = rs.getString("tags"); // Store as a single string
		            boolean isResolved = rs.getBoolean("is_resolved"); // Fetch resolved status

		            Question question = new Question(rs.getInt("id"), rs.getString("content"), user, tags);
		            question.setResolved(isResolved); // Save resolved status
		            questions.add(question);
		        }
		    }
		    return questions;
		}
		
		public List<Question> getQuestionsByTag(String tag) throws SQLException {
		    List<Question> questions = new ArrayList<>();
		    String query = "SELECT * FROM Questions WHERE tags LIKE ? ORDER BY date_created DESC";

		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setString(1, "%" + tag + "%"); // Match partial tags
		        ResultSet rs = pstmt.executeQuery();

		        while (rs.next()) {
		            User user = getUserById(rs.getInt("user_id"));
		            String tags = rs.getString("tags"); // Store tags as a single string
		            boolean isResolved = rs.getBoolean("is_resolved");

		            Question question = new Question(rs.getInt("id"), rs.getString("content"), user, tags);
		            question.setResolved(isResolved);
		            questions.add(question);
		        }
		    }
		    return questions;
		}

		
		// Mark question resolved
		
		public void markQuestionResolved(int questionId) throws SQLException {
		    String query = "UPDATE Questions SET is_resolved = TRUE WHERE id = ?";
		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setInt(1, questionId);
		        int updatedRows = pstmt.executeUpdate();
		        if (updatedRows > 0) {
		            System.out.println("Question " + questionId + " marked as resolved.");
		        } else {
		            System.out.println("⚠️ ERROR: Question ID " + questionId + " not found or update failed.");
		        }
		    }
		}
		
		public void deleteQuestion(int questionId) throws SQLException {
		    System.out.println("Attempting to delete question ID: " + questionId);

		    String query = "DELETE FROM Questions WHERE id = ?";
		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setInt(1, questionId);
		        int affectedRows = pstmt.executeUpdate();
		        if (affectedRows == 0) {
		            System.out.println("Error: No question deleted. (Already deleted?)");
		        } else {
		            System.out.println(" Question deleted successfully!");
		        }
		    }
		}
		
		// Retrieve questions sorted by date (newest first)
		public List<Question> getQuestionsSortedByDate() throws SQLException {
		    List<Question> questions = new ArrayList<>();
		    String query = "SELECT * FROM Questions ORDER BY date_created DESC";

		    try (PreparedStatement pstmt = connection.prepareStatement(query);
		         ResultSet rs = pstmt.executeQuery()) {
		        while (rs.next()) {
		            User user = getUserById(rs.getInt("user_id"));
		            String tags = rs.getString("tags");
		            boolean isResolved = rs.getBoolean("is_resolved");
		            Question question = new Question(rs.getInt("id"), rs.getString("content"), user, tags);
		            question.setResolved(isResolved);
		            questions.add(question);
		        }
		    }
		    return questions;
		}

		// Retrieve questions sorted by resolution status (unresolved first)
		public List<Question> getQuestionsSortedByStatus() throws SQLException {
		    List<Question> questions = new ArrayList<>();
		    String query = "SELECT * FROM Questions ORDER BY is_resolved ASC, date_created DESC";

		    try (PreparedStatement pstmt = connection.prepareStatement(query);
		         ResultSet rs = pstmt.executeQuery()) {
		        while (rs.next()) {
		            User user = getUserById(rs.getInt("user_id"));
		            String tags = rs.getString("tags");
		            boolean isResolved = rs.getBoolean("is_resolved");
		            Question question = new Question(rs.getInt("id"), rs.getString("content"), user, tags);
		            question.setResolved(isResolved);
		            questions.add(question);
		        }
		    }
		    return questions;
		}
		
		// update question
		public boolean updateQuestion(int questionId, String newContent) throws SQLException {
		    String query = "UPDATE Questions SET content = ? WHERE id = ?";
		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setString(1, newContent);
		        pstmt.setInt(2, questionId);
		        int rowsUpdated = pstmt.executeUpdate();
		        return rowsUpdated > 0;
		    }
		}
		
		public void ensureConnectionOpen() throws SQLException {
		    if (connection == null || connection.isClosed()) {
		        connection = DriverManager.getConnection(DB_URL, USER, PASS);
		    }
		}

		// ************ ANSWER ************ // 
		
		// Create answer 
		
		public void addAnswer(Answer answer) throws SQLException {
			System.out.println("Connection status: " + !connection.isClosed());
		    if (answer.getUser().getId() == 0) {
		        throw new SQLException("Invalid user ID. Ensure user exists in the database before adding an answer.");
		    }
		    
		    String query = "INSERT INTO Answers (content, question_id, user_id) VALUES (?, ?, ?)";
		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setString(1, answer.getContent());
		        pstmt.setInt(2, answer.getQuestion().getId());
		        pstmt.setInt(3, answer.getUser().getId()); //  Valid user ID
		        pstmt.executeUpdate();
		        System.out.println("✅ Answer inserted successfully.");
		    }
		}
		
		// Retrieve answer through ID 
		
		public Answer getAnswerById(int answerId) throws SQLException {
	        String query = "SELECT * FROM Answers WHERE id = ?";
	        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	            pstmt.setInt(1, answerId);
	            ResultSet rs = pstmt.executeQuery();

	            if (rs.next()) {
	                User user = getUserById(rs.getInt("user_id"));
	                Question question = getQuestionById(rs.getInt("question_id"));
	                return new Answer(rs.getInt("id"), rs.getString("content"), user, question);
	            }
	        }
	        return null;
	    }
		
		public void updateReview(Review review) throws SQLException {
 		    String query = "UPDATE Reviews SET content = ? WHERE id = ?";
 		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
 		        pstmt.setString(1, review.getContent());
 		        pstmt.setInt(2, review.getId());
 		        pstmt.executeUpdate();
 		    }
 		}
		
		// Retrieve answers by user ID
		public List<Answer> getAnswersByUser(int userId) throws SQLException {
		    List<Answer> answers = new ArrayList<>();
		    String query = "SELECT * FROM Answers WHERE user_id = ?";

		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setInt(1, userId);
		        ResultSet rs = pstmt.executeQuery();

		        while (rs.next()) {
		            User user = getUserById(rs.getInt("user_id"));
		            Question question = getQuestionById(rs.getInt("question_id"));
		            Answer answer = new Answer(rs.getInt("id"), rs.getString("content"), user, question);
		            answer.setSolution(rs.getBoolean("is_solution"));
		            answers.add(answer);
		        }
		    }
		    return answers;
		}



		// Marks answer as solution
		
		public void markAnswerAsSolution(int answerId) throws SQLException {
		    String query = "UPDATE Answers SET is_solution = TRUE WHERE id = ?";
		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setInt(1, answerId);
		        int updatedRows = pstmt.executeUpdate();
		        if (updatedRows > 0) {
		            System.out.println(" Answer " + answerId + " marked as a solution.");
		        } else {
		            System.out.println("⚠️ ERROR: Answer ID " + answerId + " not found or update failed.");
		        }
		    }
		}

		// Add this method to your DatabaseHelper class
		public void deleteUser(int userId) throws SQLException {
		    String deleteAnswers = "DELETE FROM Answers WHERE user_id = ?";
		    String deleteQuestions = "DELETE FROM Questions WHERE user_id = ?";
		    String deleteUser = "DELETE FROM Users WHERE id = ?";

		    try (PreparedStatement pstmtAnswers = connection.prepareStatement(deleteAnswers);
		         PreparedStatement pstmtQuestions = connection.prepareStatement(deleteQuestions);
		         PreparedStatement pstmtUser = connection.prepareStatement(deleteUser)) {
		        
		        // Delete user's answers
		        pstmtAnswers.setInt(1, userId);
		        pstmtAnswers.executeUpdate();

		        // Delete user's questions
		        pstmtQuestions.setInt(1, userId);
		        pstmtQuestions.executeUpdate();

		        // Finally, delete the user
		        pstmtUser.setInt(1, userId);
		        int affectedRows = pstmtUser.executeUpdate();
		        
		        if (affectedRows > 0) {
		            System.out.println("User deleted successfully.");
		        } else {
		            System.out.println(" No user found with ID: " + userId);
		        }
		    }
		}

		
		// Marks answer as read
		
		public void markAnswerAsRead(int userId, int answerId) throws SQLException {
	        String query = "INSERT INTO ReadAnswers (user_id, answer_id) VALUES (?, ?) ON DUPLICATE KEY UPDATE user_id = user_id";
	        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	            pstmt.setInt(1, userId);
	            pstmt.setInt(2, answerId);
	            pstmt.executeUpdate();
	        }
	    }
		
		// Retrieve answers for a specific question, sorted by date
		// Retrieve answers for a specific question, ensuring each answer has an associated Question object
		public List<Answer> getAnswersByQuestionId(int questionId) throws SQLException {
		    List<Answer> answers = new ArrayList<>();
		    String query = "SELECT * FROM Answers WHERE question_id = ? ORDER BY date_created DESC";

		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setInt(1, questionId);
		        ResultSet rs = pstmt.executeQuery();

		        // Fetch the Question object to link with Answers
		        Question relatedQuestion = getQuestionById(questionId);

		        while (rs.next()) {
		            int answerId = rs.getInt("id");
		            String content = rs.getString("content");
		            int userId = rs.getInt("user_id");

		            //  Fetch the User who posted the answer
		            User user = getUserById(userId);

		            //  Create an Answer object with the associated Question
		            Answer answer = new Answer(answerId, content, user, relatedQuestion);
		            answers.add(answer);
		        }
		    }
		    return answers;
		}

		// get sorted answers
		
		public List<Answer> getSortedAnswers(int questionId, String sortBy, int userId) throws SQLException {
		    List<Answer> answers = new ArrayList<>();
		    String query = "SELECT id, content, user_id, question_id, is_solution FROM Answers WHERE question_id = ? ORDER BY is_solution DESC, date_created DESC";

		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setInt(1, questionId);
		        ResultSet rs = pstmt.executeQuery();

		        while (rs.next()) {
		            User user = getUserById(rs.getInt("user_id"));
		            Question question = getQuestionById(rs.getInt("question_id"));
		            boolean isSolution = rs.getBoolean("is_solution"); // Ensure this fetches correctly
		            System.out.println("DEBUG: Answer ID " + rs.getInt("id") + " isSolution: " + isSolution);

		            Answer answer = new Answer(rs.getInt("id"), rs.getString("content"), user, question);
		            answer.setSolution(isSolution); // Set the correct value
		            answers.add(answer);
		        }
		    }
		    return answers;
		}
		
		public void deleteAnswer(int answerId, int userId) throws SQLException {
		    String query = "DELETE FROM Answers WHERE id = ? AND user_id = ?";
		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setInt(1, answerId);
		        pstmt.setInt(2, userId);
		        pstmt.executeUpdate();
		    }
		}
		
		// update answer 
		public boolean updateAnswer(int answerId, String newContent) throws SQLException {
			String query = "UPDATE Answers SET content = ? WHERE id = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				  pstmt.setString(1, newContent);
				  pstmt.setInt(2, answerId);
				  int rowsUpdated = pstmt.executeUpdate();
				  return rowsUpdated > 0;
				}
		}
		
		// ****************** REVIEWS ****************** //
		
		public void addReview(Review review) throws SQLException {
			String reviewQuery = "INSERT INTO Reviews (content, reviewer_id, answer_id, updated_from_review_id) VALUES (?, ?, ?, ?)";
		    String updateQuery = "INSERT INTO ReviewUpdates (review_id, reviewer_id, student_id, viewed, timestamp) VALUES (?, ?, ?, FALSE, CURRENT_TIMESTAMP)";

		    try (PreparedStatement reviewStmt = connection.prepareStatement(reviewQuery, Statement.RETURN_GENERATED_KEYS)) {
		        reviewStmt.setString(1, review.getContent());
		        reviewStmt.setInt(2, review.getReviewer().getId());
		        reviewStmt.setInt(3, review.getAnswer().getId());
		        if (review.getOriginalReview() != null) {
		            reviewStmt.setInt(4, review.getOriginalReview().getId());
		        } else {
		            reviewStmt.setNull(4, java.sql.Types.INTEGER);
		        }
		        reviewStmt.executeUpdate();

		        // Retrieve the generated review ID
		        try (ResultSet generatedKeys = reviewStmt.getGeneratedKeys()) {
		            if (generatedKeys.next()) {
		                int reviewId = generatedKeys.getInt(1);

		                // Insert into ReviewUpdates
		                try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
		                    updateStmt.setInt(1, reviewId);
		                    updateStmt.setInt(2, review.getReviewer().getId());
		                    updateStmt.setInt(3, review.getAnswer().getUser().getId()); // Assuming Answer has a student ID
		                    updateStmt.executeUpdate();
		                }
		            }
		        }
		    }
		}

		public List<Review> getReviewsByReviewer(int userId) throws SQLException {
		    List<Review> reviews = new ArrayList<>();
		    String query = "SELECT * FROM Reviews WHERE reviewer_id = ?";
		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setInt(1, userId);
		        ResultSet rs = pstmt.executeQuery();

		        while (rs.next()) {
		            User reviewer = getUserById(rs.getInt("reviewer_id"));
		            Answer answer = getAnswerById(rs.getInt("answer_id"));
		            Review original = rs.getInt("updated_from_review_id") != 0 ? getReviewById(rs.getInt("updated_from_review_id")) : null;
		            reviews.add(new Review(rs.getInt("id"), rs.getString("content"), reviewer, answer, original));
		        }
		    }
		    return reviews;
		}

		public Review getReviewById(int reviewId) throws SQLException {
		    String query = "SELECT * FROM Reviews WHERE id = ?";
		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setInt(1, reviewId);
		        ResultSet rs = pstmt.executeQuery();
		        if (rs.next()) {
		            User reviewer = getUserById(rs.getInt("reviewer_id"));
		            Answer answer = getAnswerById(rs.getInt("answer_id"));
		            Review original = rs.getInt("updated_from_review_id") != 0 ? getReviewById(rs.getInt("updated_from_review_id")) : null;
		            return new Review(reviewId, rs.getString("content"), reviewer, answer, original);
		        }
		    }
		    return null;
		}
		
		public List<Review> getReviewsByAnswerId(int answerId) throws SQLException {
		    List<Review> reviews = new ArrayList<>();
		    String query = "SELECT * FROM Reviews WHERE answer_id = ?";
		    
		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setInt(1, answerId);
		        ResultSet rs = pstmt.executeQuery();
		        
		        while (rs.next()) {
		            int reviewId = rs.getInt("id");
		            String content = rs.getString("content");
		            int reviewerId = rs.getInt("reviewer_id");
		            int originalReviewId = rs.getInt("updated_from_review_id");

		            User reviewer = getUserById(reviewerId);
		            Answer answer = getAnswerById(answerId);
		            Review originalReview = (originalReviewId != 0) ? getReviewById(originalReviewId) : null;

		            Review review = new Review(reviewId, content, reviewer, answer, originalReview);
		            reviews.add(review);
		        }
		    }

		    return reviews;
		}
		
		public List<Review> getReviewsByReviewerId(int reviewerId) throws SQLException {
		    List<Review> reviews = new ArrayList<>();
		    String query = "SELECT * FROM Reviews WHERE reviewer_id = ?";

		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setInt(1, reviewerId);
		        ResultSet rs = pstmt.executeQuery();

		        while (rs.next()) {
		            int reviewId = rs.getInt("id");
		            String content = rs.getString("content");
		            int answerId = rs.getInt("answer_id");
		            int originalReviewId = rs.getInt("updated_from_review_id");

		            Answer answer = getAnswerById(answerId);
		            User reviewer = getUserById(reviewerId);
		            Review original = (originalReviewId != 0) ? getReviewById(originalReviewId) : null;

		            reviews.add(new Review(reviewId, content, reviewer, answer, original));
		        }
		    }
		    return reviews;
		}

	///////////  Methods to Interacts with the requesting reviewer table //////////
	public List<String> getStudentsRequestingReviewerRole() throws SQLException
	{
		ArrayList<String> usernames = new ArrayList<>();
	    String query = "SELECT userName FROM studentsRequestReviewerRole";

	    try (PreparedStatement pstmt = connection.prepareStatement(query);
	         ResultSet rs = pstmt.executeQuery()) {

	        while (rs.next()) {
	            usernames.add(rs.getString("userName"));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return usernames;
	}
	
	public void requestToBeReviewer(User user) throws SQLException
	{
		String insertStudent = "INSERT INTO studentsRequestReviewerRole (userName) VALUES (?)";
		
		try(PreparedStatement pstmt = connection.prepareStatement(insertStudent))
		{
			pstmt.setString(1,user.getUserName());	
			pstmt.executeUpdate();
		}
	}
	
	public String removeReviewerRequest(User user) throws SQLException
	{
		String removeStudent = "DELETE FROM studentsRequestReviewerRole WHERE userName = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(removeStudent))
		{
			pstmt.setString(1,user.getUserName());
			int changed = pstmt.executeUpdate();
			
			if(changed > 0)
			{
				System.out.println("User: " + user.getUserName() + " succesfully deleted from the database");
				return "";
			}else {
				System.out.println("Failed to delete user");
				return "Failed to delete user.";
			}
		}catch (SQLException e )
		{
			e.printStackTrace();
			System.out.println("ERROR");
			return "ERROR";
		}
	}
	
	public boolean requestedReviewer(User user) throws SQLException
	{
		String query = "SELECT 1 FROM studentsRequestReviewerRole WHERE userName = ?";
		  
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) 
	    {
	        pstmt.setString(1, user.getUserName());
	        ResultSet rs = pstmt.executeQuery();
	        
	        
	        return rs.next();
	        
	    }catch(SQLException e)
	    {
	    	e.printStackTrace();
	    	return false;
	    }
	}

	
	public ArrayList<String> getReviewers() {
	    ArrayList<String> users = new ArrayList<>();
	    String query = "SELECT userName, role FROM cse360users WHERE role LIKE 'Reviewer'"; 

	    try {
	        if (connection == null || connection.isClosed()) { 
	            connectToDatabase();
	        }

	        try (PreparedStatement pstmt = connection.prepareStatement(query);
	             ResultSet rs = pstmt.executeQuery()) {

	            while (rs.next()) {
	                String userName = rs.getString("userName");
	                String role = rs.getString("role");
	                

	                
	                String userDetails =  userName ;
	                users.add(userDetails);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return users;
	}
    //TRUSTED REVIEWERS
	public void addTrustedReviewer(int studentId, int reviewerId, int weight) throws SQLException {
	    String checkQuery = "SELECT COUNT(*) FROM trustedreviewers WHERE student_id = ? AND reviewer_id = ?";
	    try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
	        checkStmt.setInt(1, studentId);
	        checkStmt.setInt(2, reviewerId);
	        ResultSet rs = checkStmt.executeQuery();
	        if (rs.next() && rs.getInt(1) > 0) {
	            throw new SQLException("This reviewer is already trusted for this student");
	        }

	        String insertQuery = "INSERT INTO trustedreviewers (student_id, reviewer_id, weight) VALUES (?, ?, ?)";
	        try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
	            pstmt.setInt(1, studentId);
	            pstmt.setInt(2, reviewerId);
	            pstmt.setInt(3, weight);
	            pstmt.executeUpdate();
	        }
	    }
	}
		public void removeTrustedReviewer(int studentId, int reviewerId) throws SQLException {
		    String query = "DELETE FROM trustedreviewers WHERE student_id = ? AND reviewer_id = ?";
		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setInt(1, studentId);
		        pstmt.setInt(2, reviewerId);
		        int affectedRows = pstmt.executeUpdate();
		        if (affectedRows == 0) {
		            throw new SQLException("No such trusted reviewer relationship exists");
		        }
		    }
		}
		
		public Map<User, Integer> getTrustedReviewersForStudent(int studentId) throws SQLException {
			Map<User, Integer> reviewerWeights = new HashMap<>();
		    String query = "SELECT reviewer_id, weight FROM trustedreviewers WHERE student_id = ?";
		    
		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setInt(1, studentId);
		        ResultSet rs = pstmt.executeQuery();
		        
		        while (rs.next()) {
		            int reviewerId = rs.getInt("reviewer_id");
		            int weight = rs.getInt("weight");
		            User reviewer = getUserById(reviewerId);
		            if (reviewer != null) {
		                reviewerWeights.put(reviewer, weight);
		            }
		        }
		    }
		    return reviewerWeights;
		}
		
		public boolean isTrustedReviewer(int studentId, int reviewerId) throws SQLException {
		    String query = "SELECT COUNT(*) FROM trustedreviewers WHERE student_id = ? AND reviewer_id = ?";
		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setInt(1, studentId);
		        pstmt.setInt(2, reviewerId);
		        ResultSet rs = pstmt.executeQuery();
		        return rs.next() && rs.getInt(1) > 0;
		    }
		}
		
		public List<User> getAllUserObjects() throws SQLException {
		    List<User> users = new ArrayList<>();
		    String query = "SELECT id, userName, password, email, role FROM cse360users";

		    try {
		        if (connection == null || connection.isClosed()) {
		            connectToDatabase();
		        }

		        try (PreparedStatement pstmt = connection.prepareStatement(query);
		             ResultSet rs = pstmt.executeQuery()) {

		            while (rs.next()) {
		                int id = rs.getInt("id");
		                String userName = rs.getString("userName");
		                String password = rs.getString("password");
		                String email = rs.getString("email");
		                String rolesStr = rs.getString("role");
		                
		                List<String> roles = new ArrayList<>();
		                if (rolesStr != null && !rolesStr.isEmpty()) {
		                    String[] rolesArray = rolesStr.split(",");
		                    for (String role : rolesArray) {
		                        roles.add(role.trim());
		                    }
		                }
		                
		                users.add(new User(id, userName, password, email, roles));
		            }
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		        throw e;
		    }
		    return users;
		}
		
		public void updateReviewerWeight(int studentId, int reviewerId, int newWeight) throws SQLException {
		    String query = "UPDATE trustedreviewers SET weight = ? WHERE student_id = ? AND reviewer_id = ?";
		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setInt(1, newWeight);
		        pstmt.setInt(2, studentId);
		        pstmt.setInt(3, reviewerId);
		        pstmt.executeUpdate();
		    }
		}
		
		//Review Updates
		// Fetch unviewed review updates for a student
	    public List<ReviewUpdate> getUnviewedReviewUpdates(int studentId) throws SQLException {
	        List<ReviewUpdate> updates = new ArrayList<>();
	        String query = "SELECT ru.id, r.content, u.userName, ru.review_id FROM ReviewUpdates ru " +
	                       "JOIN Reviews r ON ru.review_id = r.id " +
	                       "JOIN cse360users u ON ru.reviewer_id = u.id " +
	                       "WHERE ru.student_id = ? AND ru.viewed = FALSE " +
	                       "ORDER BY ru.timestamp DESC";

	        try (PreparedStatement stmt = connection.prepareStatement(query)) {
	            stmt.setInt(1, studentId);
	            ResultSet rs = stmt.executeQuery();
	            while (rs.next()) {
	                updates.add(new ReviewUpdate(
	                    rs.getInt("id"),
	                    rs.getString("content"),
	                    rs.getString("userName"),
	                    rs.getInt("review_id")
	                ));
	            }
	        }
	        return updates;
	    }

	    // Mark a review update as viewed
	    public void markReviewUpdateAsViewed(int updateId) throws SQLException {
	        String query = "UPDATE ReviewUpdates SET viewed = TRUE WHERE id = ?";
	        try (PreparedStatement stmt = connection.prepareStatement(query)) {
	            stmt.setInt(1, updateId);
	            stmt.executeUpdate();
	        }
	    }
	    
	    //Review feedback
	    public void addFeedback(int studentId, int reviewerId, int answerId, int reviewId, String feedback) throws SQLException {
	        String query = "INSERT INTO ReviewFeedback (student_id, reviewer_id, answer_id, review_id, feedback) VALUES (?, ?, ?, ?, ?)";
	        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	            pstmt.setInt(1, studentId);
	            pstmt.setInt(2, reviewerId);
	            pstmt.setInt(3, answerId);
	            pstmt.setInt(4, reviewId);
	            pstmt.setString(5, feedback);
	            pstmt.executeUpdate();
	        }
	    }
	    
	    public List<Feedback> getFeedbackForReviewer(int reviewerId) throws SQLException {
	        List<Feedback> feedbackList = new ArrayList<>();
	        String query = "SELECT f.id, f.student_id, f.answer_id, f.feedback, u.username " +
	                       "FROM ReviewFeedback f " +
	                       "JOIN cse360users u ON f.student_id = u.id " +
	                       "WHERE f.reviewer_id = ?";
	        
	        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	            pstmt.setInt(1, reviewerId);
	            try (ResultSet rs = pstmt.executeQuery()) {
	                while (rs.next()) {
	                    User student = getUserByUserName(rs.getString("username"));
	                    Feedback feedback = new Feedback(rs.getInt("id"), student, reviewerId, rs.getInt("answer_id"), rs.getString("feedback"));
	                    feedbackList.add(feedback);
	                }
	            }
	        }
	        return feedbackList;
	    }

	public void deleteReview(int reviewId) throws SQLException {
	        String query = "DELETE FROM Reviews WHERE id = ?";
	        try (PreparedStatement pstmt = getConnection().prepareStatement(query)) {
	            pstmt.setInt(1, reviewId);
	            pstmt.executeUpdate();
	        }
	    }
	
	// ************* STAFF *******************8 
	
	// Remove user from ProbationReviewers table
	public void removeFromProbationList(int userId) throws SQLException {
	    String query = "DELETE FROM ProbationReviewers WHERE user_id = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, userId);
	        int rowsAffected = pstmt.executeUpdate();
	        
	        if (rowsAffected > 0) {
	            System.out.println("User with ID " + userId + " removed from probation list.");
	        } else {
	            System.out.println("User with ID " + userId + " was not found in the probation list.");
	        }
	    }
	}
	
	
	//************* STAFF ******************//
	
	
		// Add user to probation list
		public void addReviewerToProbation(int userId) throws SQLException {
		    String query = "INSERT INTO ProbationReviewers (user_id) VALUES (?)";
		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setInt(1, userId);
		        pstmt.executeUpdate();
		    }
		}

		// Check if reviewer is on probation
		public boolean isReviewerOnProbation(int userId) throws SQLException {
		    String query = "SELECT * FROM ProbationReviewers WHERE user_id = ?";
		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setInt(1, userId);
		        ResultSet rs = pstmt.executeQuery();
		        return rs.next();
		    }
		}
		
		// Send Message to User
		public void sendMessage(int senderId, int recipientId, String content) throws SQLException {
		    String query = "INSERT INTO Messages (sender_id, recipient_id, content) VALUES (?, ?, ?)";

		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setInt(1, senderId);
		        pstmt.setInt(2, recipientId);
		        pstmt.setString(3, content);
		        pstmt.executeUpdate();
		    }
		}
		
		// Get all instructors
		public List<User> getAllInstructors() throws SQLException {
		    List<User> instructors = new ArrayList<>();
		    String query = "SELECT * FROM cse360users WHERE role LIKE '%instructor%'";

		    try (PreparedStatement pstmt = connection.prepareStatement(query);
		         ResultSet rs = pstmt.executeQuery()) {
		        while (rs.next()) {
		            instructors.add(getUserById(rs.getInt("id")));
		        }
		    }
		    return instructors;
		}
		
		//  get probation list
		// Returns list of Users who are on the probation list
		public List<User> getProbationList() throws SQLException {
		    List<User> probationUsers = new ArrayList<>();
		    String query = "SELECT user_id FROM ProbationReviewers";

		    try (PreparedStatement pstmt = connection.prepareStatement(query);
		         ResultSet rs = pstmt.executeQuery()) {

		        while (rs.next()) {
		            int userId = rs.getInt("user_id");
		            User user = getUserById(userId);
		            if (user != null) {
		                probationUsers.add(user);
		            }
		        }
		    }
		    return probationUsers;
		}
		
		// revoke reviewer role
		public void revokeReviewerRole(int userId) throws SQLException {
		    String getRolesQuery = "SELECT role FROM cse360users WHERE id = ?";
		    String updateRolesQuery = "UPDATE cse360users SET role = ? WHERE id = ?";

		    try (PreparedStatement getPstmt = connection.prepareStatement(getRolesQuery)) {
		        getPstmt.setInt(1, userId);
		        ResultSet rs = getPstmt.executeQuery();

		        if (rs.next()) {
		            String rolesStr = rs.getString("role");
		            List<String> rolesList = new ArrayList<>(List.of(rolesStr.split(",")));
		            rolesList.removeIf(role -> role.trim().equalsIgnoreCase("reviewer"));

		            String updatedRoles = String.join(",", rolesList);

		            try (PreparedStatement updatePstmt = connection.prepareStatement(updateRolesQuery)) {
		                updatePstmt.setString(1, updatedRoles);
		                updatePstmt.setInt(2, userId);
		                updatePstmt.executeUpdate();
		            }
		        }
		    }
		}
		
		// Delete Answer by ID for staff
		public void deleteAnswer(int answerId) throws SQLException {
		    String query = "DELETE FROM Answers WHERE id = ?";
		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setInt(1, answerId);
		        int rowsAffected = pstmt.executeUpdate();
		        
		        if (rowsAffected == 0) {
		            System.out.println("No answer found with ID: " + answerId);
		        } else {
		            System.out.println("Answer with ID " + answerId + " deleted successfully.");
		        }
		    }
		}
		
		
		// Add user to Probation List
		public void addToProbationList(int userId) throws SQLException {
		    String query = "INSERT INTO ProbationReviewers (user_id) VALUES (?)";
		    
		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setInt(1, userId);
		        pstmt.executeUpdate();
		    }
		}
		
		// Get all messages sent to a specific user
		public List<String> getMessagesForUser(int userId) throws SQLException {
		    List<String> messages = new ArrayList<>();
		    String query = "SELECT content FROM Messages WHERE recipient_id = ?";

		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setInt(1, userId);
		        ResultSet rs = pstmt.executeQuery();

		        while (rs.next()) {
		            messages.add(rs.getString("content"));
		        }
		    }
		    return messages;
		}
		
		public List<Integer> getProbationReviewers() throws SQLException {
		    List<Integer> probationReviewerIds = new ArrayList<>();
		    
		    String query = "SELECT user_id FROM ProbationReviewers";  // Correct table name

		    try (PreparedStatement pstmt = connection.prepareStatement(query);
		         ResultSet rs = pstmt.executeQuery()) {
		        
		        while (rs.next()) {
		            probationReviewerIds.add(rs.getInt("user_id"));
		        }
		    }
		    
		    return probationReviewerIds;
		}
		

}
