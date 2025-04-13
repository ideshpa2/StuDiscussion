package application;

public class Review {
    private int id;
    private String content;
    private User reviewer;
    private Answer answer;
    private Review originalReview;

    public Review(int id, String content, User reviewer, Answer answer, Review originalReview) {
        this.id = id;
        this.content = content;
        this.reviewer = reviewer;
        this.answer = answer;
        this.originalReview = originalReview;
    }

    // Getters
    public int getId() { return id; }
    public String getContent() { return content; }
    public User getReviewer() { return reviewer; }
    public Answer getAnswer() { return answer; }
    public Review getOriginalReview() { return originalReview; }

    // Setters
    public void setContent(String content) { this.content = content; }

    }


