package com.lab211.ecommerce.model;

public class ProductVariant {
    private int id;
    private int productId;
    private String color;
    private String size;
    private int stock;
    private double priceDelta;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public double getPriceDelta() { return priceDelta; }
    public void setPriceDelta(double priceDelta) { this.priceDelta = priceDelta; }
}
