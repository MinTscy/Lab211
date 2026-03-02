package com.lab211.ecommerce.model;

public class Product {
    private int id;
    private int shopId;
    private String shopName;
    private double shopRating;
    private String name;
    private String description;
    private double basePrice;
    private boolean active;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getShopId() { return shopId; }
    public void setShopId(int shopId) { this.shopId = shopId; }
    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }
    public double getShopRating() { return shopRating; }
    public void setShopRating(double shopRating) { this.shopRating = shopRating; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
