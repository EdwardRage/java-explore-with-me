package ru.practicum.ewm.categories;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.categories.dto.CategoryRequestDto;
import ru.practicum.ewm.categories.dto.CategoryResponseDto;

@Component
public class CategoryMapper {

    public Category toCategory(CategoryRequestDto categoryRequestDto) {
        Category category = new Category();
        category.setName(categoryRequestDto.getName());
        return category;
    }

    public CategoryResponseDto toCategoryDto(Category category) {
        CategoryResponseDto categoryResponseDto = new CategoryResponseDto();
        categoryResponseDto.setId(category.getId());
        categoryResponseDto.setName(category.getName());
        return categoryResponseDto;
    }
}
