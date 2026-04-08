package tr.edu.mu.se3006.business;
import tr.edu.mu.se3006.persistence.ProductRepository;
import tr.edu.mu.se3006.domain.Product;

public class OrderService {
    private ProductRepository productRepository;
    
    public OrderService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    public void placeOrder(Long productId, int quantity) {
        Product product = productRepository.findById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }
        if (product.getStock() < quantity) {
            throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
        }
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }
}
