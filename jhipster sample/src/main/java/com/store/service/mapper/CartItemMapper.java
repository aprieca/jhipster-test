package com.store.service.mapper;

import com.store.domain.CartItem;
import com.store.domain.Item;
import com.store.domain.User;
import com.store.service.dto.CartItemDTO;
import com.store.service.dto.ItemDTO;
import com.store.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CartItem} and its DTO {@link CartItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface CartItemMapper extends EntityMapper<CartItemDTO, CartItem> {
    @Mapping(target = "item", source = "item", qualifiedByName = "itemId")
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    CartItemDTO toDto(CartItem s);

    @Named("itemId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ItemDTO toDtoItemId(Item item);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
