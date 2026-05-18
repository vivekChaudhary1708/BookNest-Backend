package com.booknest.productservice.controller;

import com.booknest.productservice.dto.ProductRequest;
import com.booknest.productservice.model.Product;
import com.booknest.productservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/add")
    public String addProduct(@RequestBody ProductRequest request) {
        return productService.addProduct(request);
    }

    @GetMapping("/all")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable String id) {
        return productService.getProductById(id);
    }

    @PutMapping("/update/{id}")
    public String updateProduct(@PathVariable String id,
                                @RequestBody ProductRequest request) {
        return productService.updateProduct(id, request);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteProduct(@PathVariable String id) {
        return productService.deleteProduct(id);
    }

    @PutMapping("/stock/{id}")
    public Product updateStock(@PathVariable String id, @RequestParam int stock) {
        return productService.updateStock(id, stock);
    }
}