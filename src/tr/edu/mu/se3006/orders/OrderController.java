package tr.edu.mu.se3006.orders;

// PUBLIC: Entry point for the Orders context.
public class OrderController {
    private OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    public void handleUserRequest(Long productId, int quantity) {
        System.out.println(">>> New Request: Product ID=" + productId + ", Quantity=" + quantity);
        try {
            service.placeOrder(productId, quantity);
            System.out.println("✅ Order Confirmed\n");
        } catch (IllegalArgumentException e) {
            System.out.println("❌ ERROR: " + e.getMessage() + "\n");
        }
    }
}
