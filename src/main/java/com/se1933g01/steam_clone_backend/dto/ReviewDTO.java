package com.se1933g01.steam_clone_backend.dto;

public class ReviewDTO {
    private String authorName;
    private String content;
    private boolean isRecommended;
    private long helpful;
    private long notHelpful;

    public ReviewDTO() {
    }

    public ReviewDTO(String authorName, String content, boolean isRecommended, long helpful, long notHelpful) {
        this.authorName = authorName;
        this.content = content;
        this.isRecommended = isRecommended;
        this.helpful = helpful;
        this.notHelpful = notHelpful;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isRecommended() {
        return isRecommended;
    }

    public void setRecommended(boolean isRecommended) {
        this.isRecommended = isRecommended;
    }

    public long getHelpful() {
        return helpful;
    }

    public void setHelpful(long helpful) {
        this.helpful = helpful;
    }

    public long getNotHelpful() {
        return notHelpful;
    }

    public void setNotHelpful(long notHelpful) {
        this.notHelpful = notHelpful;
    }

}
