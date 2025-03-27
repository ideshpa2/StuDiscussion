package application;

import java.util.List;

/**
 * Represents a question posted by a user.
 */
public class Question {
    private int id;
    private String content;
    private User user;
    private String tags; 
    private boolean isResolved;

    public Question(int id, String content, User user, String tags) {
        this.id = id;
        this.content = content;
        this.user = user;
        this.tags = tags;
        this.isResolved = false;
    }

    public int getId() { return id; }
    public String getContent() { return content; }
    public User getUser() { return user; }
    public String getTags() { return tags; }
    public boolean isResolved() { return isResolved; }

    public void setResolved(boolean resolved) { this.isResolved = resolved; }

    @Override
    public String toString() {
        return (isResolved ? "✅ " : "") + content + " [" + tags + "]";
    }
}