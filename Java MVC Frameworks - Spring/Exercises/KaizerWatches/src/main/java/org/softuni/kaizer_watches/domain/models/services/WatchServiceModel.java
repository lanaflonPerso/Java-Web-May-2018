package org.softuni.kaizer_watches.domain.models.services;

import java.math.BigDecimal;

public class WatchServiceModel {

    private String id;

    private String name;

    private String imageUrl;

    private BigDecimal price;

    private String description;

    private long views;

    public WatchServiceModel() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getViews() {
        return this.views;
    }

    public void setViews(long views) {
        this.views = views;
    }
}
