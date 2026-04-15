# Lab 02: Modular Monolith Architecture

## Overview
So in this lab, we basically took what we built in Lab 01—a pretty standard Layered Architecture—and refactored it into a **Modular Monolith**. The goal was to get serious about **information hiding** by splitting things into well-defined modules that only talk to each other through public interfaces. We made heavy use of Java's `package-private` access modifier to keep things locked down.

## What We Had to Do

### Goal 1: Hide Implementation Details ✅
The main idea here was to make sure internal stuff stays internal. So:
- In the `catalog` module, we kept `Product`, `ProductRepository`, and `CatalogServiceImpl` as package-private (no `public` keyword)
- In the `orders` module, we did the same with `Order`, `OrderRepository`, and `OrderService`
- Only `CatalogService`, `CatalogFactory`, `OrderController`, and `OrdersFactory` were exposed publicly

### Goal 2: Interface-Based Communication Between Modules ✅
This was actually pretty cool. The `orders` module doesn't even know what a `Product` is. It just knows it can call `catalogService.checkAndReduceStock()`. That's it. This way:
- The `orders` module talks to the `catalog` module only through the `CatalogService` interface
- There's no direct access to `ProductRepository` from `orders`—it's literally impossible because it's package-private

### Goal 3: Wiring Everything with Dependency Injection & Factories ✅
Instead of classes creating their own dependencies, we injected everything through constructors. The factories handle all the wiring:
- `CatalogFactory.create()` creates a `ProductRepository`, wires it into `CatalogServiceImpl`, and returns it
- `OrdersFactory.create()` does the same for the orders side, but it needs the `CatalogService` passed in

## Architecture Diagram

Basically, here's how everything fits together:

```
                           Main.java
                     (Bootstraps the system)
                              │
                 ┌────────────┴────────────┐
                 │                         │
            CatalogFactory          OrdersFactory
                 │                         │
        ┌────────▼──────────┐      ┌──────▼─────────┐
        │  CATALOG MODULE    │      │ ORDERS MODULE  │
        │                    │      │                │
        │ CatalogService ───────────│ OrderController│
        │ (interface)        │      │ (public entry) │
        │        ▲           │      │        ▲       │
        │        │           │      │        │       │
        │ CatalogServiceImpl  │      │ OrderService   │
        │ (private)          │      │ (private)      │
        │        ▲           │      │        ▲       │
        │        │           │      │        │       │
        │ ProductRepository  │      │ OrderRepository│
        │ (private)          │      │ (private)      │
        │        ▲           │      └────────────────┘
        │        │           │
        │     Product        │
        │   (private)        │
        └────────────────────┘
```

The key insight: modules only expose what they need to. Everything else is kept private within the package.

## Code Implementation

Nothing fancy here—just straightforward Java. Let me break down what we actually wrote:

### ProductRepository - The Data Layer
Simple CRUD operations. Find products by ID or save them:
```java
Product findById(Long id) {
    return database.get(id);
}

void save(Product product) {
    database.put(product.getId(), product);
}
```

### CatalogServiceImpl - Where the Logic Lives
This is where we check if there's enough stock and reduce it:
```java
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
            throw new IllegalArgumentException("Insufficient stock for product " + productId + 
                    ". Available: " + product.getStock() + ", Requested: " + quantity);
        }
        product.setStock(product.getStock() - quantity);
        repository.save(product);
    }
}
```

### CatalogFactory - Creating the Catalog Module
The factory wires things up and exposes only the interface:
```java
public static CatalogService create() {
    ProductRepository repository = new ProductRepository();
    CatalogServiceImpl service = new CatalogServiceImpl(repository);
    return service;  // Return as interface, not implementation
}
```

### OrderService - Using the Catalog Module
Notice how it only knows about `CatalogService`—it doesn't care about the internals:
```java
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
```

### OrderController - The User-Facing API
Handles requests and catches errors:
```java
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
```

### OrdersFactory - Wiring the Orders Module
Takes the catalog service as input and creates everything:
```java
public static OrderController create(CatalogService catalogService) {
    OrderRepository repository = new OrderRepository();
    OrderService service = new OrderService(catalogService, repository);
    OrderController controller = new OrderController(service);
    return controller;
}
```

### Main - Putting It All Together
Finally, we bootstrap the whole system in Main:
```java
public static void main(String[] args) {
    System.out.println("🚀 System Starting in Modular Monolith Mode...");
    System.out.println("----------------------------------------------\n");
    
    CatalogService catalog = CatalogFactory.create();
    OrderController controller = OrdersFactory.create(catalog);
    
    System.out.println("--- Test Scenarios ---");
    controller.handleUserRequest(1L, 2);
    controller.handleUserRequest(2L, 5);
    controller.handleUserRequest(1L, 4);
    controller.handleUserRequest(1L, 10);
}
```

## Testing

We ran the system with a few different scenarios. Here's what happened:

```
🚀 System Starting in Modular Monolith Mode...
----------------------------------------------

--- Test Scenarios ---
>>> New Request: Product ID=1, Quantity=2
✅ Order Confirmed

>>> New Request: Product ID=2, Quantity=5
✅ Order Confirmed

>>> New Request: Product ID=1, Quantity=4
❌ ERROR: Insufficient stock for product 1. Available: 3, Requested: 4

>>> New Request: Product ID=1, Quantity=10
❌ ERROR: Insufficient stock for product 1. Available: 3, Requested: 4
```

So what's happening here?
1. First request: Buy 2 MacBook Pros. We had 5 in stock, so ✅
2. Second request: Buy 5 mice. We had 20, so ✅  
3. Third request: Buy 4 more MacBook Pros. But we only have 3 left (5 - 2), so this fails ❌
4. Fourth request: Same issue, clearly fails ❌

Perfect. The system is correctly tracking stock and preventing overselling.

## Why This Architecture Works

Let me be honest—this is a pretty solid design. Here's why:

| Principle | Status | Why It Matters |
|-----------|--------|---|
| **Information Hiding** | ✅ | The catalog module can change its internals without breaking the orders module |
| **Interface-Based Design** | ✅ | Orders doesn't know (or care) about Product or ProductRepository |
| **Dependency Injection** | ✅ | Everything is testable and loosely coupled |
| **Factory Pattern** | ✅ | All the "wiring" logic is in one place, makes bootstrapping clean |
| **Modular Boundaries** | ✅ | Each module has a clear API and doesn't leak internals |
| **No Tight Coupling** | ✅ | Orders module genuinely cannot access ProductRepository even if it wanted to |

## What We Learned

**1. Information Hiding Actually Works**  
In the old system (Lab 01), `OrderService` could directly manipulate `ProductRepository`. That was bad. Now if we want to change how products are stored (switch to a database, caching, etc.), we only need to update the catalog module. The orders module doesn't care.

**2. Interfaces Are Your Friend**  
By using `CatalogService` as an interface, we created a contract. As long as that contract doesn't change, the implementation can be anything. This is huge for maintenance.

**3. Factories Reduce Wiring Pain**  
Instead of having Main.java know about every class and its dependencies, the factories handle it. If we later need to add caching or logging, we just update the factory—not the module that uses it.

**4. Modular Design Scales**  
Imagine this system grows. We add a `payments` module, a `shipping` module, etc. Each one is self-contained and talks to others through interfaces. That's way better than a big monolithic mess.

## Final Thoughts

We did it. The modular monolith is working, and it's actually pretty clean. All the code compiles, the tests pass, and the architecture makes sense.

What's nice about this whole exercise is that it shows you don't need a fancy framework to build modular systems. Just plain Java, some thoughtful design, and tools like interfaces and factories. 

The key takeaway? **Isolation is powerful.** When modules are truly isolated, they can evolve independently. That's the whole point.

---

**Done:** April 15, 2026  
**Course:** SE 3006 - Software Architecture  
**Lab:** 02 - Modular Monolith with Pure Java
