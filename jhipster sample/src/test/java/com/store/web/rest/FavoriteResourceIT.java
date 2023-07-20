package com.store.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.store.IntegrationTest;
import com.store.domain.Favorite;
import com.store.domain.User;
import com.store.repository.FavoriteRepository;
import com.store.service.criteria.FavoriteCriteria;
import com.store.service.dto.FavoriteDTO;
import com.store.service.mapper.FavoriteMapper;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link FavoriteResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FavoriteResourceIT {

    private static final Long DEFAULT_ITEM_ID = 1L;
    private static final Long UPDATED_ITEM_ID = 2L;
    private static final Long SMALLER_ITEM_ID = 1L - 1L;

    private static final String ENTITY_API_URL = "/api/favorites";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFavoriteMockMvc;

    private Favorite favorite;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Favorite createEntity(EntityManager em) {
        Favorite favorite = new Favorite().itemId(DEFAULT_ITEM_ID);
        return favorite;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Favorite createUpdatedEntity(EntityManager em) {
        Favorite favorite = new Favorite().itemId(UPDATED_ITEM_ID);
        return favorite;
    }

    @BeforeEach
    public void initTest() {
        favorite = createEntity(em);
    }

    @Test
    @Transactional
    void createFavorite() throws Exception {
        int databaseSizeBeforeCreate = favoriteRepository.findAll().size();
        // Create the Favorite
        FavoriteDTO favoriteDTO = favoriteMapper.toDto(favorite);
        restFavoriteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(favoriteDTO)))
            .andExpect(status().isCreated());

        // Validate the Favorite in the database
        List<Favorite> favoriteList = favoriteRepository.findAll();
        assertThat(favoriteList).hasSize(databaseSizeBeforeCreate + 1);
        Favorite testFavorite = favoriteList.get(favoriteList.size() - 1);
        assertThat(testFavorite.getItemId()).isEqualTo(DEFAULT_ITEM_ID);
    }

    @Test
    @Transactional
    void createFavoriteWithExistingId() throws Exception {
        // Create the Favorite with an existing ID
        favorite.setId(1L);
        FavoriteDTO favoriteDTO = favoriteMapper.toDto(favorite);

        int databaseSizeBeforeCreate = favoriteRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFavoriteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(favoriteDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Favorite in the database
        List<Favorite> favoriteList = favoriteRepository.findAll();
        assertThat(favoriteList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkItemIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = favoriteRepository.findAll().size();
        // set the field null
        favorite.setItemId(null);

        // Create the Favorite, which fails.
        FavoriteDTO favoriteDTO = favoriteMapper.toDto(favorite);

        restFavoriteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(favoriteDTO)))
            .andExpect(status().isBadRequest());

        List<Favorite> favoriteList = favoriteRepository.findAll();
        assertThat(favoriteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFavorites() throws Exception {
        // Initialize the database
        favoriteRepository.saveAndFlush(favorite);

        // Get all the favoriteList
        restFavoriteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(favorite.getId().intValue())))
            .andExpect(jsonPath("$.[*].itemId").value(hasItem(DEFAULT_ITEM_ID.intValue())));
    }

    @Test
    @Transactional
    void getFavorite() throws Exception {
        // Initialize the database
        favoriteRepository.saveAndFlush(favorite);

        // Get the favorite
        restFavoriteMockMvc
            .perform(get(ENTITY_API_URL_ID, favorite.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(favorite.getId().intValue()))
            .andExpect(jsonPath("$.itemId").value(DEFAULT_ITEM_ID.intValue()));
    }

    @Test
    @Transactional
    void getFavoritesByIdFiltering() throws Exception {
        // Initialize the database
        favoriteRepository.saveAndFlush(favorite);

        Long id = favorite.getId();

        defaultFavoriteShouldBeFound("id.equals=" + id);
        defaultFavoriteShouldNotBeFound("id.notEquals=" + id);

        defaultFavoriteShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultFavoriteShouldNotBeFound("id.greaterThan=" + id);

        defaultFavoriteShouldBeFound("id.lessThanOrEqual=" + id);
        defaultFavoriteShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllFavoritesByItemIdIsEqualToSomething() throws Exception {
        // Initialize the database
        favoriteRepository.saveAndFlush(favorite);

        // Get all the favoriteList where itemId equals to DEFAULT_ITEM_ID
        defaultFavoriteShouldBeFound("itemId.equals=" + DEFAULT_ITEM_ID);

        // Get all the favoriteList where itemId equals to UPDATED_ITEM_ID
        defaultFavoriteShouldNotBeFound("itemId.equals=" + UPDATED_ITEM_ID);
    }

    @Test
    @Transactional
    void getAllFavoritesByItemIdIsInShouldWork() throws Exception {
        // Initialize the database
        favoriteRepository.saveAndFlush(favorite);

        // Get all the favoriteList where itemId in DEFAULT_ITEM_ID or UPDATED_ITEM_ID
        defaultFavoriteShouldBeFound("itemId.in=" + DEFAULT_ITEM_ID + "," + UPDATED_ITEM_ID);

        // Get all the favoriteList where itemId equals to UPDATED_ITEM_ID
        defaultFavoriteShouldNotBeFound("itemId.in=" + UPDATED_ITEM_ID);
    }

    @Test
    @Transactional
    void getAllFavoritesByItemIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        favoriteRepository.saveAndFlush(favorite);

        // Get all the favoriteList where itemId is not null
        defaultFavoriteShouldBeFound("itemId.specified=true");

        // Get all the favoriteList where itemId is null
        defaultFavoriteShouldNotBeFound("itemId.specified=false");
    }

    @Test
    @Transactional
    void getAllFavoritesByItemIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        favoriteRepository.saveAndFlush(favorite);

        // Get all the favoriteList where itemId is greater than or equal to DEFAULT_ITEM_ID
        defaultFavoriteShouldBeFound("itemId.greaterThanOrEqual=" + DEFAULT_ITEM_ID);

        // Get all the favoriteList where itemId is greater than or equal to UPDATED_ITEM_ID
        defaultFavoriteShouldNotBeFound("itemId.greaterThanOrEqual=" + UPDATED_ITEM_ID);
    }

    @Test
    @Transactional
    void getAllFavoritesByItemIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        favoriteRepository.saveAndFlush(favorite);

        // Get all the favoriteList where itemId is less than or equal to DEFAULT_ITEM_ID
        defaultFavoriteShouldBeFound("itemId.lessThanOrEqual=" + DEFAULT_ITEM_ID);

        // Get all the favoriteList where itemId is less than or equal to SMALLER_ITEM_ID
        defaultFavoriteShouldNotBeFound("itemId.lessThanOrEqual=" + SMALLER_ITEM_ID);
    }

    @Test
    @Transactional
    void getAllFavoritesByItemIdIsLessThanSomething() throws Exception {
        // Initialize the database
        favoriteRepository.saveAndFlush(favorite);

        // Get all the favoriteList where itemId is less than DEFAULT_ITEM_ID
        defaultFavoriteShouldNotBeFound("itemId.lessThan=" + DEFAULT_ITEM_ID);

        // Get all the favoriteList where itemId is less than UPDATED_ITEM_ID
        defaultFavoriteShouldBeFound("itemId.lessThan=" + UPDATED_ITEM_ID);
    }

    @Test
    @Transactional
    void getAllFavoritesByItemIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        favoriteRepository.saveAndFlush(favorite);

        // Get all the favoriteList where itemId is greater than DEFAULT_ITEM_ID
        defaultFavoriteShouldNotBeFound("itemId.greaterThan=" + DEFAULT_ITEM_ID);

        // Get all the favoriteList where itemId is greater than SMALLER_ITEM_ID
        defaultFavoriteShouldBeFound("itemId.greaterThan=" + SMALLER_ITEM_ID);
    }

    @Test
    @Transactional
    void getAllFavoritesByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            favoriteRepository.saveAndFlush(favorite);
            user = UserResourceIT.createEntity(em);
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        favorite.setUser(user);
        favoriteRepository.saveAndFlush(favorite);
        Long userId = user.getId();

        // Get all the favoriteList where user equals to userId
        defaultFavoriteShouldBeFound("userId.equals=" + userId);

        // Get all the favoriteList where user equals to (userId + 1)
        defaultFavoriteShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultFavoriteShouldBeFound(String filter) throws Exception {
        restFavoriteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(favorite.getId().intValue())))
            .andExpect(jsonPath("$.[*].itemId").value(hasItem(DEFAULT_ITEM_ID.intValue())));

        // Check, that the count call also returns 1
        restFavoriteMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultFavoriteShouldNotBeFound(String filter) throws Exception {
        restFavoriteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restFavoriteMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingFavorite() throws Exception {
        // Get the favorite
        restFavoriteMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFavorite() throws Exception {
        // Initialize the database
        favoriteRepository.saveAndFlush(favorite);

        int databaseSizeBeforeUpdate = favoriteRepository.findAll().size();

        // Update the favorite
        Favorite updatedFavorite = favoriteRepository.findById(favorite.getId()).get();
        // Disconnect from session so that the updates on updatedFavorite are not directly saved in db
        em.detach(updatedFavorite);
        updatedFavorite.itemId(UPDATED_ITEM_ID);
        FavoriteDTO favoriteDTO = favoriteMapper.toDto(updatedFavorite);

        restFavoriteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, favoriteDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(favoriteDTO))
            )
            .andExpect(status().isOk());

        // Validate the Favorite in the database
        List<Favorite> favoriteList = favoriteRepository.findAll();
        assertThat(favoriteList).hasSize(databaseSizeBeforeUpdate);
        Favorite testFavorite = favoriteList.get(favoriteList.size() - 1);
        assertThat(testFavorite.getItemId()).isEqualTo(UPDATED_ITEM_ID);
    }

    @Test
    @Transactional
    void putNonExistingFavorite() throws Exception {
        int databaseSizeBeforeUpdate = favoriteRepository.findAll().size();
        favorite.setId(count.incrementAndGet());

        // Create the Favorite
        FavoriteDTO favoriteDTO = favoriteMapper.toDto(favorite);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFavoriteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, favoriteDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(favoriteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Favorite in the database
        List<Favorite> favoriteList = favoriteRepository.findAll();
        assertThat(favoriteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFavorite() throws Exception {
        int databaseSizeBeforeUpdate = favoriteRepository.findAll().size();
        favorite.setId(count.incrementAndGet());

        // Create the Favorite
        FavoriteDTO favoriteDTO = favoriteMapper.toDto(favorite);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFavoriteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(favoriteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Favorite in the database
        List<Favorite> favoriteList = favoriteRepository.findAll();
        assertThat(favoriteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFavorite() throws Exception {
        int databaseSizeBeforeUpdate = favoriteRepository.findAll().size();
        favorite.setId(count.incrementAndGet());

        // Create the Favorite
        FavoriteDTO favoriteDTO = favoriteMapper.toDto(favorite);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFavoriteMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(favoriteDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Favorite in the database
        List<Favorite> favoriteList = favoriteRepository.findAll();
        assertThat(favoriteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFavoriteWithPatch() throws Exception {
        // Initialize the database
        favoriteRepository.saveAndFlush(favorite);

        int databaseSizeBeforeUpdate = favoriteRepository.findAll().size();

        // Update the favorite using partial update
        Favorite partialUpdatedFavorite = new Favorite();
        partialUpdatedFavorite.setId(favorite.getId());

        partialUpdatedFavorite.itemId(UPDATED_ITEM_ID);

        restFavoriteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFavorite.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFavorite))
            )
            .andExpect(status().isOk());

        // Validate the Favorite in the database
        List<Favorite> favoriteList = favoriteRepository.findAll();
        assertThat(favoriteList).hasSize(databaseSizeBeforeUpdate);
        Favorite testFavorite = favoriteList.get(favoriteList.size() - 1);
        assertThat(testFavorite.getItemId()).isEqualTo(UPDATED_ITEM_ID);
    }

    @Test
    @Transactional
    void fullUpdateFavoriteWithPatch() throws Exception {
        // Initialize the database
        favoriteRepository.saveAndFlush(favorite);

        int databaseSizeBeforeUpdate = favoriteRepository.findAll().size();

        // Update the favorite using partial update
        Favorite partialUpdatedFavorite = new Favorite();
        partialUpdatedFavorite.setId(favorite.getId());

        partialUpdatedFavorite.itemId(UPDATED_ITEM_ID);

        restFavoriteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFavorite.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFavorite))
            )
            .andExpect(status().isOk());

        // Validate the Favorite in the database
        List<Favorite> favoriteList = favoriteRepository.findAll();
        assertThat(favoriteList).hasSize(databaseSizeBeforeUpdate);
        Favorite testFavorite = favoriteList.get(favoriteList.size() - 1);
        assertThat(testFavorite.getItemId()).isEqualTo(UPDATED_ITEM_ID);
    }

    @Test
    @Transactional
    void patchNonExistingFavorite() throws Exception {
        int databaseSizeBeforeUpdate = favoriteRepository.findAll().size();
        favorite.setId(count.incrementAndGet());

        // Create the Favorite
        FavoriteDTO favoriteDTO = favoriteMapper.toDto(favorite);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFavoriteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, favoriteDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(favoriteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Favorite in the database
        List<Favorite> favoriteList = favoriteRepository.findAll();
        assertThat(favoriteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFavorite() throws Exception {
        int databaseSizeBeforeUpdate = favoriteRepository.findAll().size();
        favorite.setId(count.incrementAndGet());

        // Create the Favorite
        FavoriteDTO favoriteDTO = favoriteMapper.toDto(favorite);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFavoriteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(favoriteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Favorite in the database
        List<Favorite> favoriteList = favoriteRepository.findAll();
        assertThat(favoriteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFavorite() throws Exception {
        int databaseSizeBeforeUpdate = favoriteRepository.findAll().size();
        favorite.setId(count.incrementAndGet());

        // Create the Favorite
        FavoriteDTO favoriteDTO = favoriteMapper.toDto(favorite);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFavoriteMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(favoriteDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Favorite in the database
        List<Favorite> favoriteList = favoriteRepository.findAll();
        assertThat(favoriteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFavorite() throws Exception {
        // Initialize the database
        favoriteRepository.saveAndFlush(favorite);

        int databaseSizeBeforeDelete = favoriteRepository.findAll().size();

        // Delete the favorite
        restFavoriteMockMvc
            .perform(delete(ENTITY_API_URL_ID, favorite.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Favorite> favoriteList = favoriteRepository.findAll();
        assertThat(favoriteList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
