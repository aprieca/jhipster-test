package com.store.repository;

import com.store.domain.CartItem;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CartItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long>, JpaSpecificationExecutor<CartItem> {
    @Query("select cartItem from CartItem cartItem where cartItem.user.login = ?#{principal.username}")
    List<CartItem> findByUserIsCurrentUser();
}
