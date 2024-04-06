package ru.practicum.explore_with_me.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.category.dto.CategoryDto;
import ru.practicum.explore_with_me.category.dto.NewCategoryDto;
import ru.practicum.explore_with_me.category.service.CategoryService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class CategoryControllerAdmin {
    private final CategoryService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addNewCategory(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("POST: /admin/categories");
        return service.add(newCategoryDto);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(
            @PathVariable Long catId,
            @RequestBody @Valid NewCategoryDto newCategory
    ) {
        log.info("PATCH: /admin/categories/{}", catId);
        return service.update(catId, newCategory);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCategory(@PathVariable Long catId) {
        log.info("DELETE: /admin/categories/{}", catId);
        service.remove(catId);
    }
}