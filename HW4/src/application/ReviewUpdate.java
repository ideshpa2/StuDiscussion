package application;
public class ReviewUpdate {
    private int id;
    private String reviewText;
    private String reviewerName;
    private int reviewId;

    public ReviewUpdate(int id, String reviewText, String reviewerName, int reviewId) {
        this.id = id;
        this.reviewText = reviewText;
        this.reviewerName = reviewerName;
        this.reviewId = reviewId;
    }

    public int getId() {
        return id;
    }

    public String getReviewText() {
        return reviewText;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public int getReviewId() {
        return reviewId;
    }
}
