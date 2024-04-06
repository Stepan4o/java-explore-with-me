package ru.practicum.explore_with_me.category.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.explore_with_me.category.dto.CategoryDto;
import ru.practicum.explore_with_me.category.dto.NewCategoryDto;
import ru.practicum.explore_with_me.category.model.Category;

@UtilityClass
public class CategoryMapper {

    public CategoryDto toCategoryDto(Category category) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());

        return categoryDto;
    }

    public Category toCategory(NewCategoryDto newCategoryDto) {
        Category category = new Category();
        category.setName(newCategoryDto.getName());

        return category;
    }
}
