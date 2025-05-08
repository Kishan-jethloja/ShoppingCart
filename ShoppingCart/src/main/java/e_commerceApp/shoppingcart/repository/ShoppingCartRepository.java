package e_commerceApp.shoppingcart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import e_commerceApp.shoppingcart.entity.ShoppingCart;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Integer> {
    @Query("SELECT sc FROM ShoppingCart sc WHERE sc.customer.id = :customerId")
    List<ShoppingCart> findByCustomerId(@Param("customerId") int customerId);
    
    @Query("SELECT sc FROM ShoppingCart sc WHERE sc.customer.id = :customerId AND sc.product.id = :productId")
    ShoppingCart findByCustomerIdAndProductId(@Param("customerId") int customerId, @Param("productId") int productId);

    List<ShoppingCart> findByCustomerIdAndOrderIdIsNull(Integer customerId);

    ShoppingCart findByCustomerIdAndProductIdAndOrderIdIsNull(int customerId, int productId);
}
