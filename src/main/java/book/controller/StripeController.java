package book.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Collections;
import java.util.Map;

import book.dto.OrderDTO;
import book.service.IStripeService;

@RestController
@RequestMapping("/stripe")
public class StripeController {

    @Autowired
    private IStripeService stripeService;

    @PostMapping("/create-checkout-session")
    public ResponseEntity<Map<String, String>> createCheckoutSession(@RequestBody OrderDTO orderDTO) {
        String checkoutUrl = stripeService.createCheckoutSession(orderDTO);
        return ResponseEntity.ok(Collections.singletonMap("checkoutUrl", checkoutUrl));
    }
}
