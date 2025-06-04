package com.se1933g01.steam_clone_backend.entity.request;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "AddingGameRequest")
public class AddingGameRequest {
    @Id
    @Column(name = "RequestID")
    private Long requestId;

    @Column(name = "GameName")
    private String gameName;
    @Column(name = "ReleaseDate")
    private Date releaseDate;
    @Column(name = "Price")
    private double price;
    @Column(name = "ShortDescription")
    private String shortDescription;
    @Column(name = "FullDescription")
    private String fullDescription;
    @Column(name = "Os")
    private String os;
    @Column(name = "Storage")
    private String storage;
    @Column(name = "Processor")
    private String processor;
    @Column(name = "Memory")
    private String memory;
    @Column(name = "AdditionalNotes")
    private String additionalNotes;
    @Column(name = "Graphics")
    private String graphics;

    // ================ Relationships =============

    @OneToOne
    @MapsId
    @JoinColumn(name = "RequestID")
    private Request request;

    // ================ Getter & Setter =============

    public AddingGameRequest() {
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

    public void setPrice(double price) {
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

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public String getAdditionalNotes() {
        return additionalNotes;
    }

    public void setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
    }

    public String getGraphics() {
        return graphics;
    }

    public void setGraphics(String graphics) {
        this.graphics = graphics;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

}
