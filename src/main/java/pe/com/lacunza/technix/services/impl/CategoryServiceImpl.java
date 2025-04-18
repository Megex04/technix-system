package pe.com.lacunza.technix.services.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.com.lacunza.technix.api.models.response.CategoryResponse;
import pe.com.lacunza.technix.domain.entities.jpa.Category;
import pe.com.lacunza.technix.domain.repositories.jpa.CategoryRepository;
import pe.com.lacunza.technix.services.CategoryService;
import pe.com.lacunza.technix.api.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
@Slf4j
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryResponse> findAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryServiceImpl::toCategoryResponse)
                .toList();
    }

    @Override
    public Optional<Category> findCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public Category saveCategory(Category category) {
        Category categorySaved = new Category();
        categorySaved.setName(category.getName());
        categorySaved.setDescription(category.getDescription());
        return categoryRepository.save(categorySaved);
    }

    @Override
    public Category updateCategory(Long id, Category categoryDetails) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        category.setName(categoryDetails.getName());
        category.setDescription(categoryDetails.getDescription());

        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        // You might want to check if the category has associated products before deleting
        if (!category.getProducts().isEmpty()) {
            throw new DataIntegrityViolationException("Cannot delete category because it has associated products");
        }

        categoryRepository.delete(category);
    }

    public static CategoryResponse toCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
