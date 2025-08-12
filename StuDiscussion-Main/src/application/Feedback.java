package application;

public class Feedback {
    private int id;
    private User student;
    private int reviewerId;
    private int answerId;
    private String content;

    public Feedback(int id, User student, int reviewerId, int answerId, String content) {
        this.id = id;
        this.student = student;
        this.reviewerId = reviewerId;
        this.answerId = answerId;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public User getStudent() {
        return student;
    }

    public int getReviewerId() {
        return reviewerId;
    }

    public int getAnswerId() {
        return answerId;
    }

    public String getContent() {
        return content;
    }
}
