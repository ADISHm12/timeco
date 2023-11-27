package com.timeco.application.Repository;

import com.timeco.application.model.user.User;
import com.timeco.application.model.wishlist.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist , Integer> {
    Wishlist findByUser(User user);
}
