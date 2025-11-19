package com.onieto.order.repository;

import com.onieto.order.model.Order;
import com.onieto.order.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findFirstByUserEmailAndEstadoOrderByOrderDateDesc(String userEmail, OrderStatus estado);
}
