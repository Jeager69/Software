package com.cancha.futbol.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cancha.futbol.demo.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
	// Find a category that fits the given age
	java.util.Optional<Categoria> findFirstByEdadMinimaLessThanEqualAndEdadMaximaGreaterThanEqual(Integer edadMin, Integer edadMax);
}
