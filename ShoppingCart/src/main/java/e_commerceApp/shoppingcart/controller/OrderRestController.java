package e_commerceApp.shoppingcart.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import e_commerceApp.shoppingcart.entity.Customer;
import e_commerceApp.shoppingcart.entity.Order;
import e_commerceApp.shoppingcart.entity.ShoppingCart;
import e_commerceApp.shoppingcart.repository.CustomerRepository;
import e_commerceApp.shoppingcart.repository.OrderRepository;
import e_commerceApp.shoppingcart.repository.ShoppingCartRepository;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/orders")
public class OrderRestController {

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private ShoppingCartRepository cartRepo;

    @GetMapping
    public List<Order> getOrders() {
        return orderRepo.findAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<Order> getOrder(@PathVariable int id) {
        Order order = orderRepo.findById(id).orElse(null);

        if (order == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(order);
    }

    @PostMapping
    public ResponseEntity<Object> createOrder(
            @Valid @RequestBody Order order,
            BindingResult result) {

        if (result.hasErrors()) {
            var errorList = result.getAllErrors();
            var errorsMap = new HashMap<String, String>();

            for (int i = 0; i < errorList.size(); i++) {
                var error = (FieldError) errorList.get(i);
                errorsMap.put(error.getField(), error.getDefaultMessage());
            }

            return ResponseEntity.badRequest().body(errorsMap);
        }

        // Check if customer exists
        Customer customer = customerRepo.findById(order.getCustomer().getId()).orElse(null);
        if (customer == null) {
            return ResponseEntity.badRequest().body("Customer not found");
        }

        // Check if all cart items exist and are available
        for (ShoppingCart cartItem : order.getCartItems()) {
            ShoppingCart existingItem = cartRepo.findById(cartItem.getId()).orElse(null);
            if (existingItem == null) {
                return ResponseEntity.badRequest().body("Cart item not found: " + cartItem.getId());
            }
        }

        // Create new order
        Order newOrder = new Order();
        newOrder.setOrderDescription(order.getOrderDescription());
        newOrder.setCustomer(customer);
        newOrder.setCartItems(order.getCartItems());

        // Save the order
        Order savedOrder = orderRepo.save(newOrder);

        // Clear the cart items after order is placed
        for (ShoppingCart cartItem : order.getCartItems()) {
            cartRepo.delete(cartItem);
        }

        return ResponseEntity.ok(savedOrder);
    }

    @PutMapping("{id}")
    public ResponseEntity<Object> updateOrder(
            @PathVariable int id,
            @Valid @RequestBody Order order,
            BindingResult result) {

        Order existingOrder = orderRepo.findById(id).orElse(null);

        if (existingOrder == null) {
            return ResponseEntity.notFound().build();
        }

        if (result.hasErrors()) {
            var errorList = result.getAllErrors();
            var errorsMap = new HashMap<String, String>();

            for (int i = 0; i < errorList.size(); i++) {
                var error = (FieldError) errorList.get(i);
                errorsMap.put(error.getField(), error.getDefaultMessage());
            }

            return ResponseEntity.badRequest().body(errorsMap);
        }

        // Update order details
        existingOrder.setOrderDescription(order.getOrderDescription());
        existingOrder.setCustomer(order.getCustomer());
        existingOrder.setCartItems(order.getCartItems());

        orderRepo.save(existingOrder);
        return ResponseEntity.ok(existingOrder);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deleteOrder(@PathVariable int id) {
        Order order = orderRepo.findById(id).orElse(null);

        if (order == null) {
            return ResponseEntity.notFound().build();
        }

        orderRepo.delete(order);
        return ResponseEntity.ok().build();
    }
} 