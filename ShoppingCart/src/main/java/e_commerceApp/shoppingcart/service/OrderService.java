package e_commerceApp.shoppingcart.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import e_commerceApp.shoppingcart.entity.Order;
import e_commerceApp.shoppingcart.entity.Product;
import e_commerceApp.shoppingcart.entity.ShoppingCart;
import e_commerceApp.shoppingcart.repository.OrderRepository;
import e_commerceApp.shoppingcart.repository.ProductRepository;
import e_commerceApp.shoppingcart.repository.ShoppingCartRepository;

@Service
public class OrderService {
    
    public static class CartResult {
        private final BigDecimal amount;
        private final String error;
        
        private CartResult(BigDecimal amount, String error) {
            this.amount = amount;
            this.error = error;
        }
        
        public static CartResult success(BigDecimal amount) {
            return new CartResult(amount, null);
        }
        
        public static CartResult error(String message) {
            return new CartResult(null, message);
        }
        
        public boolean isSuccess() {
            return error == null;
        }
        
        public BigDecimal getAmount() {
            return amount;
        }
        
        public String getError() {
            return error;
        }
    }

    private OrderRepository orderRepository;
    private ProductRepository productRepository;
    private ShoppingCartRepository cartRepository;

    public OrderService(OrderRepository orderRepository, 
                       ProductRepository productRepository,
                       ShoppingCartRepository cartRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
    }

    @Transactional(readOnly = true)
    public Order getOrderDetail(int orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    @Transactional
    public CartResult getCartAmount(List<ShoppingCart> cartItems) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        for (ShoppingCart cart : cartItems) {
            if (cart == null || cart.getProduct() == null || cart.getProduct().getId() <= 0) {
                return CartResult.error("Invalid product in cart");
            }
            
            Product productFromDb = productRepository.findById(cart.getProduct().getId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + cart.getProduct().getId()));
            
            if (productFromDb.getPrice() == null) {
                return CartResult.error("Product price is not set for product: " + productFromDb.getName());
            }
            
            if (cart.getQuantity() <= 0) {
                return CartResult.error("Invalid quantity for product: " + productFromDb.getName());
            }

            // Check if enough quantity is available
            if (productFromDb.getAvailableQuantity() < cart.getQuantity()) {
                return CartResult.error(String.format(
                    "Not enough quantity available for product '%s'. Requested: %d, Available: %d",
                    productFromDb.getName(),
                    cart.getQuantity(),
                    productFromDb.getAvailableQuantity()
                ));
            }
            
            // Calculate item amount and set it
            BigDecimal itemAmount = productFromDb.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity()));
            cart.setAmount(itemAmount);
            cart.setProductName(productFromDb.getName());
            cart.setPrice(productFromDb.getPrice());
            totalAmount = totalAmount.add(itemAmount);
        }
        
        return CartResult.success(totalAmount);
    }

    @Transactional
    public CartResult saveOrder(Order order) {
        // Calculate total amount from all cart items
        BigDecimal totalAmount = order.getCartItems().stream()
            .map(cart -> {
                // Calculate amount for each item
                BigDecimal itemAmount = cart.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity()));
                cart.setAmount(itemAmount);
                return itemAmount;
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Set the total amount
        order.setTotalAmount(totalAmount);
        
        // Save the order first
        Order savedOrder = orderRepository.save(order);
        
        // Update product quantities and save cart items
        for (ShoppingCart cart : order.getCartItems()) {
            Product product = cart.getProduct();
            product.setAvailableQuantity(product.getAvailableQuantity() - cart.getQuantity());
            productRepository.save(product);
            
            // Set the order reference and save the cart item
            cart.setOrder(savedOrder);
            cartRepository.save(cart);
        }
        
        return CartResult.success(totalAmount);
    }
    
    @Transactional
    public void deleteOrder(int orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderId));
        
        // First, delete or update all associated cart items
        List<ShoppingCart> cartItems = order.getCartItems();
        if (cartItems != null) {
            for (ShoppingCart cartItem : cartItems) {
                // Return the quantity back to product's available quantity
                Product product = cartItem.getProduct();
                product.setAvailableQuantity(product.getAvailableQuantity() + cartItem.getQuantity());
                productRepository.save(product);
                
                // Delete the cart item
                cartRepository.delete(cartItem);
            }
        }
        
        // Now we can safely delete the order
        orderRepository.delete(order);
    }
}
