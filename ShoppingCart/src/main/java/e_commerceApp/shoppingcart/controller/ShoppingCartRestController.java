package e_commerceApp.shoppingcart.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import e_commerceApp.shoppingcart.Utils.DateUtil;
import e_commerceApp.shoppingcart.dto.CustomerDTO;
import e_commerceApp.shoppingcart.dto.OrderDTO;
import e_commerceApp.shoppingcart.dto.OrderDetailDTO;
import e_commerceApp.shoppingcart.dto.ResponseOrderDTO;
import e_commerceApp.shoppingcart.dto.CartItemResponseDTO;
import e_commerceApp.shoppingcart.entity.Customer;
import e_commerceApp.shoppingcart.entity.Order;
import e_commerceApp.shoppingcart.entity.Product;
import e_commerceApp.shoppingcart.entity.ShoppingCart;
import e_commerceApp.shoppingcart.repository.ShoppingCartRepository;
import e_commerceApp.shoppingcart.service.CustomerService;
import e_commerceApp.shoppingcart.service.OrderService;
import e_commerceApp.shoppingcart.service.ProductService;

@RestController
@RequestMapping("/api")
public class ShoppingCartRestController {

    private OrderService orderService;
    private ProductService productService;
    private CustomerService customerService;
    private ShoppingCartRepository cartRepository;

    public ShoppingCartRestController(OrderService orderService, 
                                   ProductService productService, 
                                   CustomerService customerService,
                                   ShoppingCartRepository cartRepository) {
        this.orderService = orderService;
        this.productService = productService;
        this.customerService = customerService;
        this.cartRepository = cartRepository;
    }

    @PostMapping("/createProduct")
    public ResponseEntity<?> createProduct(@RequestBody Product product, @RequestParam int customerId) {
        // Check if customer is admin
        Customer customer = customerService.getCustomerById(customerId)
            .orElse(null);
        if (customer == null || !customer.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Only admin users can create products");
        }

        Product savedProduct = productService.createProduct(product);
        return ResponseEntity.ok(savedProduct);
    }

    @PutMapping("/updateProduct/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable int id, @RequestBody Product product, @RequestParam int customerId) {
        // Check if customer is admin
        Customer customer = customerService.getCustomerById(customerId)
            .orElse(null);
        if (customer == null || !customer.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Only admin users can update products");
        }

        Product existingProduct = productService.getProductById(id);
        if (existingProduct == null) {
            return ResponseEntity.notFound().build();
        }
        product.setId(id);
        Product updatedProduct = productService.updateProduct(product);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/deleteProduct/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable int id, @RequestParam int customerId) {
        // Check if customer is admin
        Customer customer = customerService.getCustomerById(customerId)
            .orElse(null);
        if (customer == null || !customer.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Access Denied: Only administrators can delete products");
        }

        Product existingProduct = productService.getProductById(id);
        if (existingProduct == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Product with ID " + id + " was not found");
        }
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product has been successfully deleted from the inventory");
    }

    @GetMapping("/getProduct/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable int id) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    @GetMapping(value = "/getAllProducts")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> productList = productService.getAllProducts();
        return ResponseEntity.ok(productList);
    }
    
    @GetMapping(value = "/getAllCustomers")
    public ResponseEntity<?> getAllCustomers() {
        List<Customer> customerList = customerService.getAllCustomer();
        if (customerList.isEmpty()) {
            return ResponseEntity.ok("No customers found in the system");
        }
        List<CustomerDTO> dtoList = customerList.stream()
            .map(CustomerDTO::fromEntity)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @PostMapping("/createCustomer")
    public ResponseEntity<?> createCustomer(@RequestBody Customer customer) {
        return customerService.saveCustomer(customer);
    }

    @PutMapping("/updateCustomer/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable int id, @RequestBody Customer customer) {
        return customerService.getCustomerById(id)
                .map(existingCustomer -> {
                    customer.setId(id);
                    return customerService.saveCustomer(customer);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/deleteCustomer/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable int id) {
        return customerService.getCustomerById(id)
                .map(customer -> {
                    customerService.deleteCustomer(customer);
                    return ResponseEntity.ok("Customer deleted successfully");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/getCustomer/{id}")
    public ResponseEntity<CustomerDTO> getCustomer(@PathVariable int id) {
        return customerService.getCustomerById(id)
                .map(customer -> ResponseEntity.ok(CustomerDTO.fromEntity(customer)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/getOrder/{orderId}")
    public ResponseEntity<?> getOrderDetails(@PathVariable int orderId) {
        Order order = orderService.getOrderDetail(orderId);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(OrderDetailDTO.fromEntity(order));
    }
  
   
    @PostMapping("/placeOrder")
    public ResponseEntity<?> placeOrder(@RequestBody OrderDTO orderDTO) {
        ResponseOrderDTO responseOrderDTO = new ResponseOrderDTO();

        // Get customer by ID
        Customer customer = customerService.getCustomerById(orderDTO.getCustomerId())
            .orElse(null);
        if (customer == null) {
            return ResponseEntity.badRequest().body("Error: Customer ID " + orderDTO.getCustomerId() + " not found in the system");
        }
        
        // Get all cart items for this customer that aren't part of an order yet
        List<ShoppingCart> cartItems = cartRepository.findByCustomerIdAndOrderIdIsNull(orderDTO.getCustomerId());
        if (cartItems.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: No items in cart for customer: " + customer.getName());
        }
        
        // Create new order
        Order order = new Order();
        order.setCustomer(customer);
        order.setCartItems(cartItems);
        order.setOrderDescription(orderDTO.getOrderDescription());
        
        // Save order and get result
        OrderService.CartResult result = orderService.saveOrder(order);
        
        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result.getError());
        }

        // Set response data
        responseOrderDTO.setAmount(result.getAmount());
        responseOrderDTO.setDate(DateUtil.getCurrentDateTime());
        responseOrderDTO.setInvoiceNumber(new Random().nextInt(1000));
        responseOrderDTO.setOrderId(order.getId());
        responseOrderDTO.setOrderDescription(order.getOrderDescription());

        return ResponseEntity.ok(responseOrderDTO);
    }

    @DeleteMapping("/deleteorder/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable int orderId) {
        try {
            orderService.deleteOrder(orderId);
            return ResponseEntity.ok("Order #" + orderId + " has been successfully deleted");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to delete order #" + orderId + ": " + e.getMessage());
        }
    }
	
    @PostMapping("/addToCart")
    public ResponseEntity<?> addToCart(@RequestBody ShoppingCart cart) {
        // Validate customer exists
        Customer customer = customerService.getCustomerById(cart.getCustomer().getId())
            .orElse(null);
        if (customer == null) {
            return ResponseEntity.badRequest().body("Error: Customer ID " + cart.getCustomer().getId() + " not found in the system");
        }

        // Validate product exists
        Product product = productService.getProductById(cart.getProduct().getId());
        if (product == null) {
            return ResponseEntity.badRequest().body("Error: Product ID " + cart.getProduct().getId() + " not found in the system");
        }

        // Check if product already exists in customer's cart
        ShoppingCart existingCart = cartRepository.findByCustomerIdAndProductIdAndOrderIdIsNull(
            customer.getId(), 
            product.getId()
        );

        if (existingCart != null) {
            // Update existing cart item
            int newQuantity = existingCart.getQuantity() + cart.getQuantity();
            
            // Check if enough quantity is available
            if (product.getAvailableQuantity() < cart.getQuantity()) {
                return ResponseEntity.badRequest()
                    .body("Insufficient stock: Only " + product.getAvailableQuantity() + " units available for " + product.getName());
            }

            existingCart.setQuantity(newQuantity);
            existingCart.setAmount(product.getPrice().multiply(BigDecimal.valueOf(newQuantity)));
            ShoppingCart savedCart = cartRepository.save(existingCart);
            return ResponseEntity.ok(CartItemResponseDTO.fromEntity(savedCart));
        }

        // If product doesn't exist in cart, create new cart item
        // Check if enough quantity is available
        if (product.getAvailableQuantity() < cart.getQuantity()) {
            return ResponseEntity.badRequest()
                .body("Insufficient stock: Only " + product.getAvailableQuantity() + " units available for " + product.getName());
        }

        // Set the product and customer
        cart.setProduct(product);
        cart.setCustomer(customer);
        cart.setPrice(product.getPrice());
        cart.setProductName(product.getName());
        
        // Calculate initial amount
        cart.setAmount(product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));

        // Save the cart item
        ShoppingCart savedCart = cartRepository.save(cart);
        return ResponseEntity.ok(CartItemResponseDTO.fromEntity(savedCart));
    }

    @GetMapping("/getCartItems/{customerId}")
    public ResponseEntity<?> getCartItems(@PathVariable int customerId) {
        // First check if customer exists
        Customer customer = customerService.getCustomerById(customerId)
            .orElse(null);
        if (customer == null) {
            return ResponseEntity.badRequest().body("Error: Customer ID " + customerId + " not found in the system");
        }

        List<ShoppingCart> cartItems = cartRepository.findByCustomerIdAndOrderIdIsNull(customerId);
        if (cartItems.isEmpty()) {
            return ResponseEntity.ok("Shopping cart is empty for customer: " + customer.getName());
        }

        List<CartItemResponseDTO> dtoList = cartItems.stream()
            .map(CartItemResponseDTO::fromEntity)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @DeleteMapping("/removeFromCart/{cartId}")
    public ResponseEntity<String> removeFromCart(@PathVariable int cartId) {
        ShoppingCart cart = cartRepository.findById(cartId).orElse(null);
        if (cart == null) {
            return ResponseEntity.notFound().build();
        }

        // If the cart item is part of an order, we shouldn't modify product quantity
        if (cart.getOrder() == null) {
            // Return the quantity back to product's available quantity
            Product product = cart.getProduct();
            product.setAvailableQuantity(product.getAvailableQuantity() + cart.getQuantity());
            productService.updateProduct(product);
        }

        cartRepository.delete(cart);
        return ResponseEntity.ok("Item has been successfully removed from your shopping cart");
    }
}