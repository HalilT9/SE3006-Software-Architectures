package tr.edu.mu.se3006;
import tr.edu.mu.se3006.presentation.OrderController;
import tr.edu.mu.se3006.business.OrderService;
import tr.edu.mu.se3006.persistence.ProductRepository;

public class Main {
    public static void main(String[] args) {
        System.out.println("🚀 System Starting...\n");
        
        // Create the lowest layer (ProductRepository)
        ProductRepository repository = new ProductRepository();
        
        // Create the middle layer (OrderService) and inject the repository
        OrderService service = new OrderService(repository);
        
        // Create the top layer (OrderController) and inject the service
        OrderController controller = new OrderController(service);
        
        System.out.println("--- Test Scenarios ---");
        // Test the system
        controller.handleUserRequest(1L, 2);
        controller.handleUserRequest(2L, 5);
        controller.handleUserRequest(1L, 10);
        controller.handleUserRequest(999L, 1);
    }
}
