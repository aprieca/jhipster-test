package com.store.service.mapper;

import com.store.domain.Category;
import com.store.domain.Item;
import com.store.service.dto.CategoryDTO;
import com.store.service.dto.ItemDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Item} and its DTO {@link ItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface ItemMapper extends EntityMapper<ItemDTO, Item> {
    @Mapping(target = "category", source = "category", qualifiedByName = "categoryId")
    ItemDTO toDto(Item s);

    @Named("categoryId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CategoryDTO toDtoCategoryId(Category category);
}
