package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerIdOrderByIdAsc(Long ownerId);

    List<Item> findAllByOwnerIdOrderByIdAsc(Long ownerId, Pageable pageable);

    List<Item> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableTrue(String nameSearch,
                                                                           String descriptionSearch,
                                                                           Pageable pageable);

    List<Item> findAllByRequestIdIn(List<Long> requestIds);
}
