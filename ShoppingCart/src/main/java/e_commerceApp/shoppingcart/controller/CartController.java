package e_commerceApp.shoppingcart.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import e_commerceApp.shoppingcart.dto.CartItemDTO;
import e_commerceApp.shoppingcart.dto.CartItemRequestDTO;
import e_commerceApp.shoppingcart.entity.Customer;
import e_commerceApp.shoppingcart.entity.Product;
import e_commerceApp.shoppingcart.entity.ShoppingCart;
import e_commerceApp.shoppingcart.repository.CustomerRepository;
import e_commerceApp.shoppingcart.repository.ProductRepository;
import e_commerceApp.shoppingcart.repository.ShoppingCartRepository;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ShoppingCartRepository cartRepo;

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private CustomerRepository customerRepo;

    @GetMapping("/customer/{customerId}")
    public List<CartItemDTO> getCartItems(@PathVariable int customerId) {
        return cartRepo.findByCustomerId(customerId)
                .stream()
                .map(CartItemDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCartItem(@PathVariable int id) {
        ShoppingCart cartItem = cartRepo.findById(id).orElse(null);

        if (cartItem == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Cart item with ID " + id + " was not found");
        }

        return ResponseEntity.ok(CartItemDTO.fromEntity(cartItem));
    }

    @PostMapping("/addToCart")
    public ResponseEntity<?> addToCart(@RequestBody CartItemRequestDTO requestDTO) {
        // Validate request
        if (requestDTO.getCustomerId() == null || requestDTO.getProductId() == null || requestDTO.getQuantity() == null) {
            return ResponseEntity.badRequest().body("Customer ID, Product ID, and Quantity are required");
        }

        // Get customer
        Customer customer = customerRepo.findById(requestDTO.getCustomerId()).orElse(null);
        if (customer == null) {
            return ResponseEntity.badRequest().body("Customer not found with ID: " + requestDTO.getCustomerId());
        }

        // Get product
        Product product = productRepo.findById(requestDTO.getProductId()).orElse(null);
        if (product == null) {
            return ResponseEntity.badRequest().body("Product not found with ID: " + requestDTO.getProductId());
        }

        // Check if product is already in cart
        ShoppingCart existingItem = cartRepo.findByCustomerIdAndProductId(customer.getId(), product.getId());

        if (existingItem != null) {
            // Update quantity of existing item
            int newQuantity = existingItem.getQuantity() + requestDTO.getQuantity();
            if (newQuantity > product.getAvailableQuantity()) {
                return ResponseEntity.badRequest().body(
                    String.format("Not enough quantity available for product '%s'. Requested: %d, Available: %d",
                        product.getName(),
                        newQuantity,
                        product.getAvailableQuantity())
                );
            }
            existingItem.setQuantity(newQuantity);
            existingItem.setPrice(product.getPrice());
            existingItem.setAmount(product.getPrice().multiply(BigDecimal.valueOf(newQuantity)));
            ShoppingCart savedItem = cartRepo.save(existingItem);
            return ResponseEntity.ok(CartItemDTO.fromEntity(savedItem));
        }

        // Check if requested quantity is available for new item
        if (requestDTO.getQuantity() > product.getAvailableQuantity()) {
            return ResponseEntity.badRequest().body(
                String.format("Not enough quantity available for product '%s'. Requested: %d, Available: %d",
                    product.getName(),
                    requestDTO.getQuantity(),
                    product.getAvailableQuantity())
            );
        }

        // Create new cart item
        ShoppingCart cartItem = new ShoppingCart();
        cartItem.setCustomer(customer);
        cartItem.setProduct(product);
        cartItem.setProductName(product.getName());
        cartItem.setQuantity(requestDTO.getQuantity());
        cartItem.setPrice(product.getPrice());
        cartItem.setAmount(product.getPrice().multiply(BigDecimal.valueOf(requestDTO.getQuantity())));
        ShoppingCart savedItem = cartRepo.save(cartItem);

        return ResponseEntity.ok(CartItemDTO.fromEntity(savedItem));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateCartItem(@PathVariable Integer id, @RequestBody CartItemRequestDTO requestDTO) {
        // Check if cart item exists
        ShoppingCart existingItem = cartRepo.findById(id).orElse(null);
        if (existingItem == null) {
            return ResponseEntity.badRequest().body("Cart item not found with ID: " + id);
        }

        // Check if product exists
        Product product = productRepo.findById(requestDTO.getProductId()).orElse(null);
        if (product == null) {
            return ResponseEntity.badRequest().body("Product not found with ID: " + requestDTO.getProductId());
        }

        // Check if requested quantity is available
        if (requestDTO.getQuantity() > product.getAvailableQuantity()) {
            return ResponseEntity.badRequest().body(
                String.format("Not enough quantity available for product '%s'. Requested: %d, Available: %d",
                    product.getName(),
                    requestDTO.getQuantity(),
                    product.getAvailableQuantity())
            );
        }

        // Update cart item
        existingItem.setProduct(product);
        existingItem.setProductName(product.getName());
        existingItem.setQuantity(requestDTO.getQuantity());
        existingItem.setPrice(product.getPrice());
        existingItem.setAmount(product.getPrice().multiply(BigDecimal.valueOf(requestDTO.getQuantity())));
        
        ShoppingCart updatedItem = cartRepo.save(existingItem);
        return ResponseEntity.ok(CartItemDTO.fromEntity(updatedItem));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeFromCart(@PathVariable int id) {
        ShoppingCart cartItem = cartRepo.findById(id).orElse(null);

        if (cartItem == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Cart item with ID " + id + " was not found");
        }

        // Convert to DTO before deleting
        CartItemDTO removedItem = CartItemDTO.fromEntity(cartItem);
        cartRepo.delete(cartItem);
        
        return ResponseEntity.ok(Map.of(
            "message", "Item removed successfully",
            "removedItem", removedItem
        ));
    }
} 