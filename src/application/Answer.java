package application;

/**
 * Represents an answer posted to a question.
 */
public class Answer {
    private int id;
    private String content;
    private User user;
    private Question question;
    private boolean isSolution;
    private boolean isRead;

    public Answer(int id, String content, User user, Question question) {
        this.id = id;
        this.content = content;
        this.user = user;
        this.question = question;
        this.isSolution = false;
        this.isRead = false;
    }

    // Getters
    public int getId() { return id; }
    public String getContent() { return content; }
    public User getUser() { return user; }
    public Question getQuestion() { return question; }
    public boolean isSolution() { return isSolution; }
    public boolean isRead() { return isRead; }

    // Setters
    public void setSolution(boolean solution) { this.isSolution = solution; }
    public void markAsRead() { this.isRead = true; }

    // Convert to String for display
    @Override
    public String toString() {
        return "A: " + content + (isSolution ? " ✅ (Solution)" : "");
    }
}