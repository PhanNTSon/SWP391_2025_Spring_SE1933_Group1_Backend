package com.se1933g01.steam_clone_backend.dto;

import java.sql.Date;
import java.util.List;

public class GameDetailDTO {
    private String gameName;
    private Date releaseDate;
    private double price;
    private String shortDescription;
    private String fullDescription;
    private String publisherName;
    private List<ReviewDTO> reviewList;

    public GameDetailDTO() {
    }

    public GameDetailDTO(String gameName, Date releaseDate, double price, String shortDescription,
            String fullDescription, String publisherName, List<ReviewDTO> reviewList) {
        this.gameName = gameName;
        this.releaseDate = releaseDate;
        this.price = price;
        this.shortDescription = shortDescription;
        this.fullDescription = fullDescription;
        this.publisherName = publisherName;
        this.reviewList = reviewList;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getFullDescription() {
        return fullDescription;
    }

    public void setFullDescription(String fullDescription) {
        this.fullDescription = fullDescription;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public List<ReviewDTO> getreviewList() {
        return reviewList;
    }

    public void setreviewList(List<ReviewDTO> reviewList) {
        this.reviewList = reviewList;
    }

    
}
