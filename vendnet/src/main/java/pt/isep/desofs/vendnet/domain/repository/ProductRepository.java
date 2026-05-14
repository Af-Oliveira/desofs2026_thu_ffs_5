package pt.isep.desofs.vendnet.domain.repository;

import java.util.List;
import java.util.Optional;
import pt.isep.desofs.vendnet.domain.model.product.Product;

public interface ProductRepository {
	Optional<Product> findById(Long id);

	Optional<Product> findBySku(String sku);

	List<Product> findAllByActiveTrue();

	Product save(Product product);
}
