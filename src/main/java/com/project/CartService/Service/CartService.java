package com.project.CartService.Service;

import com.project.CartService.DTO.ProductDTO;
import com.project.CartService.DTO.UserDTO;
import com.project.CartService.Exception.ResourceNotFoundException;
import com.project.CartService.Exception.UnauthorizedUserException;
import com.project.CartService.Model.Cart;
import com.project.CartService.Model.CartItem;
import com.project.CartService.Repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;


@Service
    public class CartService {

        @Autowired
        private CartRepository cartRepository;


        @Autowired
        private RestTemplate restTemplate;



    public Cart addItemToCart(Long userId, CartItem item) {
    // Verify if the user exists
    String userServiceUrl = "http://USER-SERVICE/users/" + userId;
        UserDTO userDTO;
        try {
            userDTO = restTemplate.getForObject(userServiceUrl, UserDTO.class);
            System.out.println(userDTO.getRole());
            if (userDTO == null || userDTO.getRole() == null || !userDTO.getRole().toString().equalsIgnoreCase("USER")) {
                throw new UnauthorizedUserException("Only users can add items to the cart.");
            }
    } catch (HttpClientErrorException.NotFound ex) {
        throw new ResourceNotFoundException("User with ID " + userId + " does not exist.");
    }

    // Verify if the product exists
    String productServiceUrl = "http://PRODUCT-SERVICE/products/" + item.getProductId();
    try {
        restTemplate.getForObject(productServiceUrl, ProductDTO.class);
    } catch (HttpClientErrorException.NotFound ex) {
        throw new ResourceNotFoundException("Product with ID " + item.getProductId() + " does not exist.");
    }

    // Proceed with cart operations
    Cart cart = cartRepository.findByUserId(userId).orElse(new Cart());

        cart.setUserId(userId);
    cart.getItems().add(item);
    return cartRepository.save(cart);
}

    public Cart removeItemFromCart(Long userId, Long itemId) {
            Optional<Cart> cartOpt = cartRepository.findByUserId(userId);
            if (cartOpt.isPresent()) {
                Cart cart = cartOpt.get();
                cart.getItems().removeIf(i -> i.getId().equals(itemId));
                return cartRepository.save(cart);
            }
            return null;
        }



    public void emptyCart(Long userId) {
            Optional<Cart> cartOpt = cartRepository.findByUserId(userId);
            cartOpt.ifPresent(cart -> {
                cart.getItems().clear();
                cartRepository.save(cart);
            });
        }

public Cart getOrCreateCartByUserId(Long userId) {
    try {
        // Check if the user exists by calling the User Service
        String userServiceUrl = "http://USER-SERVICE/users/" + userId;
        UserDTO userDTO = restTemplate.getForObject(userServiceUrl, UserDTO.class);

        // Proceed only if the user exists
        if (userDTO == null) {
            throw new ResourceNotFoundException("User with ID " + userId + " does not exist.");
        }
    } catch (HttpClientErrorException.NotFound ex) {
        // Handle 404 from the User Service
        throw new ResourceNotFoundException("User with ID " + userId + " does not exist.");
    }

    // If user exists, return their cart or create a new one
    return cartRepository.findByUserId(userId).orElseGet(() -> {
        Cart newCart = new Cart();
        newCart.setUserId(userId);
        return cartRepository.save(newCart);
    });
}

    public Cart updateCartItemQuantity(Long userId, Long productId, int newQuantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user ID: " + userId));

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Product not found in cart: " + productId));

        cartItem.setQuantity(newQuantity);
        cartRepository.save(cart);
        return cart;
    }
}





