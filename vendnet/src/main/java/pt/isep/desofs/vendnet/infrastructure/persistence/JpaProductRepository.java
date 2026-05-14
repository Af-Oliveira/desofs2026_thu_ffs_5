package pt.isep.desofs.vendnet.infrastructure.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.isep.desofs.vendnet.domain.model.product.Product;

@Repository
public interface JpaProductRepository
		extends JpaRepository<Product, Long>,
				pt.isep.desofs.vendnet.domain.repository.ProductRepository {

	Optional<Product> findBySku(String sku);
}
