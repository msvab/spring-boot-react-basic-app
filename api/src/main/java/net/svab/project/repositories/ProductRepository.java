package net.svab.project.repositories;

import net.svab.project.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findById(Long id);

    Collection<Product> findAllByOrderByNameAsc();
}
