package tr.edu.mu.se3006.catalog;

// Package-private implementation. Hidden from the outside world.
class CatalogServiceImpl implements CatalogService {
    private ProductRepository repository;
    
    CatalogServiceImpl(ProductRepository repository) {
        this.repository = repository;
    }
    
    @Override
    public void checkAndReduceStock(Long productId, int quantity) {
        Product product = repository.findById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found: " + productId);
        }
        if (product.getStock() < quantity) {
            throw new IllegalArgumentException("Insufficient stock for product " + productId + ". Available: " + product.getStock() + ", Requested: " + quantity);
        }
        product.setStock(product.getStock() - quantity);
        repository.save(product);
    }
}
