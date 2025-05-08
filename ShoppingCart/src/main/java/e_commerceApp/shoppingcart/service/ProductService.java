package e_commerceApp.shoppingcart.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import e_commerceApp.shoppingcart.entity.Product;
import e_commerceApp.shoppingcart.repository.ProductRepository;

@Service
public class ProductService {

	 private ProductRepository productRepository;

	    public ProductService(ProductRepository productRepository) {
	        this.productRepository = productRepository;
	    }

	    @Transactional(readOnly = true)
	    public List<Product> getAllProducts() {
	        return this.productRepository.findAll();
	    }

	    @Transactional(readOnly = true)
	    public Product getProductById(int id) {
	        Optional<Product> product = this.productRepository.findById(id);
	        return product.orElse(null);
	    }

	    @Transactional
	    public Product createProduct(Product product) {
	        return this.productRepository.save(product);
	    }

	    @Transactional
	    public Product updateProduct(Product product) {
	        return this.productRepository.save(product);
	    }

	    @Transactional
	    public void deleteProduct(int id) {
	        this.productRepository.deleteById(id);
	    }
}
