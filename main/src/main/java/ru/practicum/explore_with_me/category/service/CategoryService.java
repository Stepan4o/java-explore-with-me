package ru.practicum.explore_with_me.category.service;

import ru.practicum.explore_with_me.category.dto.CategoryDto;
import ru.practicum.explore_with_me.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto add(NewCategoryDto newCategoryDto);

    void remove(long id);

    CategoryDto update(long id, NewCategoryDto newCategory);

    CategoryDto getById(long id);

    List<CategoryDto> getCategories(int from, int size);
}

