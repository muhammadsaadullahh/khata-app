package com.khataapp.controller;

import com.khataapp.dto.CategoryRequest;
import com.khataapp.dto.CategoryResponse;
import com.khataapp.model.KhataItem;
import com.khataapp.repository.KhataRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final KhataRepository repository;
    public CategoryController(KhataRepository repository) { this.repository = repository; }
    @GetMapping public List<CategoryResponse> list(Authentication auth) {
        return repository.findCategories(auth.getName()).stream().map(this::response).toList();
    }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public CategoryResponse create(@Valid @RequestBody CategoryRequest request, Authentication auth) {
        KhataItem item = new KhataItem(); item.setPk("USER#" + auth.getName()); item.setSk("CATEGORY#" + UUID.randomUUID());
        item.setItemType("CATEGORY"); item.setUserId(auth.getName()); item.setCategoryId(item.getSk().substring(9));
        item.setCategory(request.name().trim()); item.setSystemCategory(false); repository.save(item); return response(item);
    }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable String id, Authentication auth) {
        repository.findCategories(auth.getName()).stream().filter(c -> id.equals(c.getCategoryId()) && !Boolean.TRUE.equals(c.getSystemCategory()))
                .findFirst().ifPresent(repository::delete);
    }
    private CategoryResponse response(KhataItem i) { return new CategoryResponse(i.getCategoryId(), i.getCategory(), Boolean.TRUE.equals(i.getSystemCategory())); }
}
