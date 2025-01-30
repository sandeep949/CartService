package com.project.CartService.Controller;


import com.project.CartService.DTO.UserDTO;
import com.project.CartService.Exception.ResourceNotFoundException;
import com.project.CartService.Exception.UnauthorizedUserException;
import com.project.CartService.Model.Cart;
import com.project.CartService.Model.CartItem;
import com.project.CartService.Service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
    @RequestMapping("/cart")
    public class CartController {

    @Autowired
    private CartService cartService;


    @Autowired
    private RestTemplate restTemplate;


    @PostMapping("/{userId}")
    public ResponseEntity<Cart> addItemToCart(@RequestBody CartItem item, @PathVariable("userId") Long userId) {
        Cart updatedCart = cartService.addItemToCart(userId, item);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedCart);
    }


    @DeleteMapping("/remove/{userId}/{itemId}")
    public ResponseEntity<Cart> removeItemFromCart(
            @PathVariable Long userId,
            @PathVariable Long itemId) {
        Cart updatedCart = cartService.removeItemFromCart(userId, itemId);
        return ResponseEntity.ok(updatedCart);
    }

        @DeleteMapping("/clear")
        public ResponseEntity<Void> emptyCart(@RequestParam Long userId) {
            cartService.emptyCart(userId);
            return ResponseEntity.noContent().build();
        }


    @GetMapping("/{userId}")
    public ResponseEntity<Cart> getOrCreateCartByUserId(@PathVariable Long userId) {

            Cart cart = cartService.getOrCreateCartByUserId(userId);
            return ResponseEntity.ok(cart);

    }


}
