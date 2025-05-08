package e_commerceApp.shoppingcart.dto;

import java.util.List;

import e_commerceApp.shoppingcart.entity.ShoppingCart;

public class OrderDTO {
    private Integer customerId;
    private String orderDescription;
    private List<ShoppingCart> cartItems;

    public OrderDTO() {
    }

    public OrderDTO(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getOrderDescription() {
        return orderDescription;
    }

    public void setOrderDescription(String orderDescription) {
        this.orderDescription = orderDescription;
    }

    public List<ShoppingCart> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<ShoppingCart> cartItems) {
        this.cartItems = cartItems;
    }
    
    @Override
    public String toString() {
        return "OrderDTO{" +
                "customerId=" + customerId +
                ", orderDescription='" + orderDescription + '\'' +
                ", cartItems=" + cartItems +
                '}';
    }
}
