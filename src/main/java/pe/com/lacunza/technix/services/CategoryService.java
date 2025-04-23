package pe.com.lacunza.technix.services;

import pe.com.lacunza.technix.api.models.response.CategoryResponse;
import pe.com.lacunza.technix.domain.entities.jpa.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    List<CategoryResponse> findAllCategories();

    Optional<Category> findCategoryById(Long id);

    Category saveCategory(Category category);

    Category updateCategory(Long id, Category categoryDetails);

    void deleteCategory(Long id);
}
