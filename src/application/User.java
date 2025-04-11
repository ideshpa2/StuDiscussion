package application;

import java.util.ArrayList;
import java.util.List;

/**
 * The User class represents a user entity in the system.
 * It contains the user's details such as userName, password, email, and roles.
 */
public class User {
	private int id = -1;
    private String userName;
    private String password;
    private String email; // Added email field
    private List<String> roles; // Changed role from String to List<String> to support multiple roles
    private List<Question> questions;
    private List<Answer> answers;
    
    // Constructor to initialize a new User object with userName, password, email, and roles.
    public User(int id, String userName, String password, String email, List<String> roles) {
    	this.id = id;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.roles = new ArrayList<>(roles); // Ensure safe copying of roles
        this.questions = new ArrayList<>();
        this.answers = new ArrayList<>();
    }

    // Sets the roles of the user.
    public void setRoles(List<String> roles) {
        this.roles = new ArrayList<>(roles);
    }

    // Adds a single role to the user.
    public void addRole(String role) {
        if (!roles.contains(role)) {
            roles.add(role);
        }
    }

    // Removes a specific role from the user.
    public void removeRole(String role) {
        roles.remove(role);
    }
    
    public int getId() { return id; }
    public String getUserName() { return userName; }
    public String getPassword() { return password; }
    public String getEmail() { return email; } // New method to get email
    public List<String> getRoles() { return roles; } // Return list of roles
    public List<Question> getQuestions() { return questions; }
    public List<Answer> getAnswers() {return answers; }
    
    public void setId(int id) { this.id = id; }
    
    // Add a question to the user's list
    public void addQuestion(Question question) {
        questions.add(question);
    }

    // Add an answer to the user's list
    public void addAnswer(Answer answer) {
        answers.add(answer);
    }
    

}
