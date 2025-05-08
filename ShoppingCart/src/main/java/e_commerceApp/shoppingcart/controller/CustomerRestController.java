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
import e_commerceApp.shoppingcart.repository.CustomerRepository;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/customers")
public class CustomerRestController {

	@Autowired
	private CustomerRepository repo;
	
	  @GetMapping
	public List<Customer> getCustomers() {
		return repo.findAll();
	}

	@GetMapping("{id}")
	public ResponseEntity<Customer> getCustomer(@PathVariable int id) {
		Customer customer = repo.findById(id).orElse(null);

		if (customer == null) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(customer);
		 }

	  @PostMapping
	public ResponseEntity<Object> createCustomer(
			@Valid @RequestBody Customer customer,
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

		repo.save(customer);
		return ResponseEntity.ok(customer);
	}

	@PutMapping("{id}")
	public ResponseEntity<Object> updateCustomer(
			@PathVariable int id,
			@Valid @RequestBody Customer customer,
			BindingResult result) {

		Customer existingCustomer = repo.findById(id).orElse(null);

		if (existingCustomer == null) {
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

		existingCustomer.setName(customer.getName());
		existingCustomer.setEmail(customer.getEmail());

		repo.save(existingCustomer);
		return ResponseEntity.ok(existingCustomer);
	}

	@DeleteMapping("{id}")
	public ResponseEntity<Object> deleteCustomer(@PathVariable int id) {
		Customer customer = repo.findById(id).orElse(null);

		if (customer == null) {
			return ResponseEntity.notFound().build();
		}

		repo.delete(customer);
		return ResponseEntity.ok().build();
	}
}
