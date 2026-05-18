package com.booknest.productservice.service;

import com.booknest.productservice.dto.ProductRequest;
import com.booknest.productservice.model.Product;
import com.booknest.productservice.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @PostConstruct
    public void seedDefaultBooks() {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            java.io.InputStream is = getClass().getResourceAsStream("/books-seed.json");
            if (is != null) {
                List<Product> books = mapper.readValue(is, new com.fasterxml.jackson.core.type.TypeReference<List<Product>>() {});
                List<Product> existingProducts = productRepository.findAll();
                
                // Delete dummy books
                List<Product> dummyBooks = existingProducts.stream()
                        .filter(p -> p.getName() != null && p.getName().contains("Essentials Vol."))
                        .collect(java.util.stream.Collectors.toList());
                if (!dummyBooks.isEmpty()) {
                    productRepository.deleteAll(dummyBooks);
                    existingProducts.removeAll(dummyBooks);
                }

                List<String> existingNames = existingProducts.stream()
                        .map(Product::getName)
                        .filter(n -> n != null)
                        .map(String::toLowerCase)
                        .collect(java.util.stream.Collectors.toList());
                
                List<Product> toSave = new ArrayList<>();
                for (Product b : books) {
                    if (b.getName() != null && !existingNames.contains(b.getName().toLowerCase())) {
                        toSave.add(b);
                        existingNames.add(b.getName().toLowerCase());
                    }
                }
                if (!toSave.isEmpty()) {
                    productRepository.saveAll(toSave);
                }
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<String> categories = Arrays.asList(
                "Fiction", "Non-Fiction", "Science", "Technology", "History",
                "Biography", "Self-Help", "Romance", "Mystery", "Thriller",
                "Fantasy", "Horror", "Children", "Educational", "Comics",
                "Poetry", "Philosophy", "Religion", "Art", "Business"
        );

        List<Product> existingProducts = productRepository.findAll();
        Map<String, Integer> countByCategory = new HashMap<>();
        for (String category : categories) {
            countByCategory.put(category, 0);
        }
        for (Product existing : existingProducts) {
            String key = existing.getCategory();
            if (key != null && countByCategory.containsKey(key)) {
                countByCategory.put(key, countByCategory.get(key) + 1);
            }
        }

        for (String category : categories) {
            int current = countByCategory.get(category);
            if (current >= 10) {
                continue;
            }
            for (int i = current + 1; i <= 10; i++) {
                String title = category + " Essentials Vol. " + i;
                boolean exists = existingProducts.stream()
                        .anyMatch(p -> p.getName() != null && p.getName().equalsIgnoreCase(title));
                if (exists) {
                    continue;
                }
                Product generated = build(
                        title,
                        "Curated " + category + " reading collection book " + i + " for BookNest readers.",
                        299 + (i * 25),
                        category,
                        10 + i,
                        "https://placehold.co/600x900/1E293B/F59E0B?text=" + title.replace(" ", "%20")
                );
                productRepository.save(generated);
            }
        }
    }

    public String addProduct(ProductRequest request) {

        Product product = new Product();

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(request.getCategory());
        product.setStock(request.getStock());
        product.setImageUrl(request.getImageUrl());

        productRepository.save(product);

        return "Product Added Successfully";
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(String id) {
        return productRepository.findById(id).orElse(null);
    }

    public String deleteProduct(String id) {
        productRepository.deleteById(id);
        return "Product Deleted Successfully";
    }

    public String updateProduct(String id, ProductRequest request) {

        Product product = productRepository.findById(id).orElse(null);

        if (product == null) {
            return "Product Not Found";
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(request.getCategory());
        product.setStock(request.getStock());
        product.setImageUrl(request.getImageUrl());

        productRepository.save(product);

        return "Product Updated Successfully";
    }

    public Product updateStock(String id, int stock) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            return null;
        }
        product.setStock(Math.max(0, stock));
        return productRepository.save(product);
    }

    private Product build(String name, String description, double price, String category, int stock, String imageUrl) {
        Product p = new Product();
        p.setName(name);
        p.setDescription(description);
        p.setPrice(price);
        p.setCategory(category);
        p.setStock(stock);
        p.setImageUrl(imageUrl);
        return p;
    }
}