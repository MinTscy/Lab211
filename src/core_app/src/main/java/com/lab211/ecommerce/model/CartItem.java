package com.lab211.ecommerce.model;

public class CartItem {
    private int variantId;
    private int productId;
    private String productName;
    private String color;
    private String size;
    private double unitPrice;
    private int quantity;

    public int getVariantId() { return variantId; }
    public void setVariantId(int variantId) { this.variantId = variantId; }
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
