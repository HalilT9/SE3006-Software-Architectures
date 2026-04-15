package tr.edu.mu.se3006.orders;
import tr.edu.mu.se3006.catalog.CatalogService;

class OrderService {
    private CatalogService catalogService;
    private OrderRepository repository;
    
    OrderService(CatalogService catalogService, OrderRepository repository) {
        this.catalogService = catalogService;
        this.repository = repository;
    }
    
    void placeOrder(Long productId, int quantity) {
        catalogService.checkAndReduceStock(productId, quantity);
        Order order = new Order(productId, quantity);
        repository.save(order);
    }
}
