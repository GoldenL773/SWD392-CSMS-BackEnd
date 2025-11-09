package fu.se.swd392csms.repository;

import fu.se.swd392csms.entity.Ingredient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository_v1 extends JpaRepository<Ingredient, Long>  {
    Page<Ingredient> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
