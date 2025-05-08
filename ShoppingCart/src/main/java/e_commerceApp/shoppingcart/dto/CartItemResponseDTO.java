package e_commerceApp.shoppingcart.dto;

import java.math.BigDecimal;
import e_commerceApp.shoppingcart.entity.ShoppingCart;

public class CartItemResponseDTO {
    private int id;
    private String productName;
    private int quantity;
    private BigDecimal price;
    private BigDecimal amount;

    public static CartItemResponseDTO fromEntity(ShoppingCart cart) {
        CartItemResponseDTO dto = new CartItemResponseDTO();
        dto.setId(cart.getId());
        dto.setProductName(cart.getProductName());
        dto.setQuantity(cart.getQuantity());
        dto.setPrice(cart.getPrice());
        dto.setAmount(cart.getAmount());
        return dto;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
} 