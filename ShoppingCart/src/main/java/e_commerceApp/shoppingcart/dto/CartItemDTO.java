package e_commerceApp.shoppingcart.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CartItemDTO {
    private int id;
    private int customerId;
    private int productId;
    private String productName;
    private int quantity;
    private BigDecimal price;
    private BigDecimal amount;
    private LocalDateTime createdAt;

    public CartItemDTO() {
    }

    public CartItemDTO(int id, int customerId, int productId, String productName, 
                      int quantity, BigDecimal price, BigDecimal amount, LocalDateTime createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    // Static factory method to create DTO from entity
    public static CartItemDTO fromEntity(e_commerceApp.shoppingcart.entity.ShoppingCart cart) {
        return new CartItemDTO(
            cart.getId(),
            cart.getCustomer().getId(),
            cart.getProduct().getId(),
            cart.getProductName(),
            cart.getQuantity(),
            cart.getPrice(),
            cart.getAmount(),
            cart.getCreatedAt()
        );
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
} 