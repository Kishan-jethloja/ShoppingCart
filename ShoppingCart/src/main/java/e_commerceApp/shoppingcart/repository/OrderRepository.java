package e_commerceApp.shoppingcart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import e_commerceApp.shoppingcart.entity.Order;

public interface OrderRepository extends JpaRepository<Order,Integer> {

}
