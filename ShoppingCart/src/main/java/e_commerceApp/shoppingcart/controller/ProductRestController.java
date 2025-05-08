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

import e_commerceApp.shoppingcart.entity.Product;
import e_commerceApp.shoppingcart.repository.ProductRepository;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/products")
public class ProductRestController {

	@Autowired
	private ProductRepository repo;
	
	@GetMapping
	public List<Product> getProducts(){
		return repo.findAll();
	}
	
	@GetMapping("{id}")
	public ResponseEntity<Product> getProduct(@PathVariable int id){
		
		Product product = repo.findById(id).orElse(null);
		
		if(product == null) {
			return ResponseEntity.notFound().build();
		}
		
		return ResponseEntity.ok(product);
	}
	
	
	@PostMapping
	public ResponseEntity<Object> createProduct(
			@Valid @RequestBody Product c_product,
			BindingResult result
			){
		
		
		if(result.hasErrors()) {
			var errorList = result.getAllErrors();
			var errorsMap = new HashMap<String, String>();
			
			for(int i = 0; i< errorList.size(); i++) {
				var error= (FieldError) errorList.get(i);
				errorsMap.put(error.getField(), error.getDefaultMessage());
			}
			
			return ResponseEntity.badRequest().body(errorsMap);
		}
		
		// Check if product with same name exists
		Product existingProduct = repo.findByName(c_product.getName()).orElse(null);
		
		if (existingProduct != null) {
			// Update existing product's quantity
			existingProduct.setAvailableQuantity(existingProduct.getAvailableQuantity() + c_product.getAvailableQuantity());
			repo.save(existingProduct);
			return ResponseEntity.ok(existingProduct);
		}
		
		// Create new product if no existing product found
		Product product = new Product();
		product.setName(c_product.getName());
		product.setPrice(c_product.getPrice());
		product.setAvailableQuantity(c_product.getAvailableQuantity());
		
		repo.save(product);
		
		return ResponseEntity.ok(product);
	}
	
	@PutMapping("{id}")
	public ResponseEntity<Object> updateProduct(
			@PathVariable int id,
			@Valid @RequestBody Product c_product,
			BindingResult result
			){
		
		Product product = repo.findById(id).orElse(null);
		
		if(product == null) {
			return ResponseEntity.notFound().build();
		}
		
		
		if(result.hasErrors()) {
			var errorList = result.getAllErrors();
			var errorsMap = new HashMap<String, String>();
			
			for(int i = 0; i< errorList.size(); i++) {
				var error= (FieldError) errorList.get(i);
				errorsMap.put(error.getField(), error.getDefaultMessage());
			}
			
			return ResponseEntity.badRequest().body(errorsMap);
		}
		
		product.setName(c_product.getName());
		product.setAvailableQuantity(c_product.getAvailableQuantity());
		product.setPrice(c_product.getPrice());
		
		repo.save(product);
		
		return ResponseEntity.ok(product);
	}
	
	@DeleteMapping("{id}")
	public ResponseEntity<Object> deleteProduct(@PathVariable int id){
		
		Product product = repo.findById(id).orElse(null);
		
		if(product == null) {
			return ResponseEntity.notFound().build();
		}
			
		repo.delete(product);
		return ResponseEntity.ok().build();
	}
}
