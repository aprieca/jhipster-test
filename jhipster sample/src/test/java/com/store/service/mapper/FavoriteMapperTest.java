package com.store.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FavoriteMapperTest {

    private FavoriteMapper favoriteMapper;

    @BeforeEach
    public void setUp() {
        favoriteMapper = new FavoriteMapperImpl();
    }
}
