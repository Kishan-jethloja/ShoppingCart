package e_commerceApp.shoppingcart.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import e_commerceApp.shoppingcart.entity.Customer;
import e_commerceApp.shoppingcart.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public ResponseEntity<?> saveCustomer(Customer customer) {
        // Check if customer with same email already exists
        if (customer.getId() == 0) { // Only check for new customers
            List<Customer> existingCustomers = customerRepository.findByEmail(customer.getEmail());
            if (!existingCustomers.isEmpty()) {
                return ResponseEntity.ok()
                    .body("Customer with email " + customer.getEmail() + " already exists");
            }
        }
        
        // Encode the password before saving
        String encodedPassword = passwordEncoder.encode(customer.getPassword());
        customer.setPassword(encodedPassword);

        Customer savedCustomer = customerRepository.save(customer);
        return ResponseEntity.ok(savedCustomer);
    }
    
    @Transactional(readOnly = true)
    public Optional<Customer> getCustomerById(int id) {
        return customerRepository.findById(id);
    }
    
    @Transactional
    public ResponseEntity<Customer> deleteCustomer(Customer customer){
    	customerRepository.delete(customer);
    	return ResponseEntity.ok().build();
    }
    
    @Transactional(readOnly = true)
    public List<Customer> getAllCustomer() {
        return customerRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Integer isCustomerPresent(Customer customer){
        if (customer == null || customer.getEmail() == null || customer.getName() == null) {
            return null;
        }
        return customerRepository.findByEmailAndName(customer.getEmail(), customer.getName())
                .map(Customer::getId)
                .orElse(null);
    }

    public Customer updateCustomer(Customer customer) {
        // If password is being updated, encode it
        if (customer.getPassword() != null && !customer.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(customer.getPassword());
            customer.setPassword(encodedPassword);
        }
        return customerRepository.save(customer);
    }

    public void deleteCustomer(int id) {
        customerRepository.deleteById(id);
    }

    public List<Customer> findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }
}
