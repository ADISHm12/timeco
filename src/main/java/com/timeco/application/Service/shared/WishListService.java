package com.timeco.application.Service.shared;

import com.timeco.application.Repository.WishlistRepository;
import com.timeco.application.model.product.Product;
import com.timeco.application.model.user.User;
import com.timeco.application.model.wishlist.Wishlist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WishListService {

    @Autowired
    private WishlistRepository  wishlistRepository;

    public boolean isProductInWishlist(User user, Product product) {
        Wishlist wishlist = user.getWishlist(); // Assuming the User entity has a getWishlist() method

        if (wishlist == null) {
            // If the user doesn't have a wishlist, the product can't be in it
            return false;
        }

        // Check if the product is in the wishlist
        return wishlist.getProducts().contains(product);
    }


    public Wishlist findWishList(User user) {
        Wishlist wishlist =wishlistRepository.findByUser(user);
        return wishlist;
    }
}
