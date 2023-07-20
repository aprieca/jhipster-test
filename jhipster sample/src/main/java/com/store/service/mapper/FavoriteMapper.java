package com.store.service.mapper;

import com.store.domain.Favorite;
import com.store.domain.User;
import com.store.service.dto.FavoriteDTO;
import com.store.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Favorite} and its DTO {@link FavoriteDTO}.
 */
@Mapper(componentModel = "spring")
public interface FavoriteMapper extends EntityMapper<FavoriteDTO, Favorite> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    FavoriteDTO toDto(Favorite s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
