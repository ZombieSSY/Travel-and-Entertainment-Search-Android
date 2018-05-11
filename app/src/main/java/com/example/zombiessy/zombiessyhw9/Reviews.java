package com.example.zombiessy.zombiessyhw9;

public class Reviews {
    private String reviewsAuthor;
    private String reviewsAuthorUrl;
    private String reviewsProfile;
    private double reviewsRating;
    private Long reviewsTime;
    private String yelpTime;
    private String reviewsText;

    public Reviews(String reviewsAuthor, String reviewsAuthorUrl, String reviewsProfile, double reviewsRating, Long reviewsTime, String reviewsText) {
        this.reviewsAuthor = reviewsAuthor;
        this.reviewsAuthorUrl = reviewsAuthorUrl;
        this.reviewsProfile = reviewsProfile;
        this.reviewsRating = reviewsRating;
        this.reviewsTime = reviewsTime;
        this.reviewsText = reviewsText;
    }

    public Reviews(String reviewsAuthor, String reviewsAuthorUrl, String reviewsProfile, double reviewsRating, Long reviewsTime, String yelpTime, String reviewsText) {
        this.reviewsAuthor = reviewsAuthor;
        this.reviewsAuthorUrl = reviewsAuthorUrl;
        this.reviewsProfile = reviewsProfile;
        this.reviewsRating = reviewsRating;
        this.reviewsTime = reviewsTime;
        this.yelpTime = yelpTime;
        this.reviewsText = reviewsText;
    }

    public String getYelpTime() {
        return yelpTime;
    }

    public void setYelpTime(String yelpTime) {
        this.yelpTime = yelpTime;
    }

    public String getReviewsAuthor() {
        return reviewsAuthor;
    }

    public void setReviewsAuthor(String reviewsAuthor) {
        this.reviewsAuthor = reviewsAuthor;
    }

    public String getReviewsAuthorUrl() {
        return reviewsAuthorUrl;
    }

    public void setReviewsAuthorUrl(String reviewsAuthorUrl) {
        this.reviewsAuthorUrl = reviewsAuthorUrl;
    }

    public String getReviewsProfile() {
        return reviewsProfile;
    }

    public void setReviewsProfile(String reviewsProfile) {
        this.reviewsProfile = reviewsProfile;
    }

    public double getReviewsRating() {
        return reviewsRating;
    }

    public void setReviewsRating(double reviewsRating) {
        this.reviewsRating = reviewsRating;
    }

    public Long getReviewsTime() {
        return reviewsTime;
    }

    public void setReviewsTime(Long reviewsTime) {
        this.reviewsTime = reviewsTime;
    }

    public String getReviewsText() {
        return reviewsText;
    }

    public void setReviewsText(String reviewsText) {
        this.reviewsText = reviewsText;
    }
}
