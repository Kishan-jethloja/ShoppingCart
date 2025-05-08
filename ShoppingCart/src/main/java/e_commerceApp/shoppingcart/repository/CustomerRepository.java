package e_commerceApp.shoppingcart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import e_commerceApp.shoppingcart.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    @Query("SELECT c FROM Customer c WHERE c.email = :email")
    List<Customer> findByEmail(@Param("email") String email);
    
    @Query("SELECT c FROM Customer c WHERE c.email = :email AND c.name = :name")
    Optional<Customer> findByEmailAndName(@Param("email") String email, @Param("name") String name);
}
