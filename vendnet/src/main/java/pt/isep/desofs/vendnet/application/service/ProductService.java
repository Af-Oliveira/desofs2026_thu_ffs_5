package pt.isep.desofs.vendnet.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pt.isep.desofs.vendnet.domain.model.product.Product;
import pt.isep.desofs.vendnet.domain.repository.ProductRepository;
import pt.isep.desofs.vendnet.infrastructure.file.FileStorageService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;

    public List<Product> findAllActive() {
        return productRepository.findAllByActiveTrue();
    }

    public Product findBySku(String sku) {
        return productRepository.findBySku(sku)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + sku));
    }

    public Product createProduct(String name, String description, BigDecimal price,
                                  String sku, MultipartFile image) {
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = fileStorageService.store(image, "products");
        }

        LocalDateTime now = LocalDateTime.now();
        Product product = Product.builder()
                .name(name)
                .description(description)
                .price(price)
                .sku(sku)
                .imageUrl(imageUrl)
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        return productRepository.save(product);
    }
}
