package com.store.service;

import com.store.domain.*; // for static metamodels
import com.store.domain.CartItem;
import com.store.repository.CartItemRepository;
import com.store.service.criteria.CartItemCriteria;
import com.store.service.dto.CartItemDTO;
import com.store.service.mapper.CartItemMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link CartItem} entities in the database.
 * The main input is a {@link CartItemCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link CartItemDTO} or a {@link Page} of {@link CartItemDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CartItemQueryService extends QueryService<CartItem> {

    private final Logger log = LoggerFactory.getLogger(CartItemQueryService.class);

    private final CartItemRepository cartItemRepository;

    private final CartItemMapper cartItemMapper;

    public CartItemQueryService(CartItemRepository cartItemRepository, CartItemMapper cartItemMapper) {
        this.cartItemRepository = cartItemRepository;
        this.cartItemMapper = cartItemMapper;
    }

    /**
     * Return a {@link List} of {@link CartItemDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<CartItemDTO> findByCriteria(CartItemCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<CartItem> specification = createSpecification(criteria);
        return cartItemMapper.toDto(cartItemRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link CartItemDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CartItemDTO> findByCriteria(CartItemCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<CartItem> specification = createSpecification(criteria);
        return cartItemRepository.findAll(specification, page).map(cartItemMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CartItemCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<CartItem> specification = createSpecification(criteria);
        return cartItemRepository.count(specification);
    }

    /**
     * Function to convert {@link CartItemCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<CartItem> createSpecification(CartItemCriteria criteria) {
        Specification<CartItem> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), CartItem_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), CartItem_.name));
            }
            if (criteria.getCategoryName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCategoryName(), CartItem_.categoryName));
            }
            if (criteria.getQuantity() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getQuantity(), CartItem_.quantity));
            }
            if (criteria.getPrice() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPrice(), CartItem_.price));
            }
            if (criteria.getItemId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getItemId(), root -> root.join(CartItem_.item, JoinType.LEFT).get(Item_.id))
                    );
            }
            if (criteria.getUserId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getUserId(), root -> root.join(CartItem_.user, JoinType.LEFT).get(User_.id))
                    );
            }
        }
        return specification;
    }
}
