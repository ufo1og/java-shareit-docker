package ru.practicum.shareit.item;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;

import java.util.Collections;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRepositoryTest {
    private final ItemRepository itemRepository;

    @Test
    public void testFindAllByOwnerIdOrderByIdAsc_WhenUserHaveItems_ThenReturnItems() {
        List<Item> expectedItems = List.of(new Item(1L, "Дрель", "Привет соседям", true, 1L, null),
                new Item(2L, "Отвертка", "Чтобы закрутить", true, 1L, null),
                new Item(3L, "Гаечный ключ", "На 17-19", true, 1L, null));

        List<Item> foundItems = itemRepository.findAllByOwnerIdOrderByIdAsc(1L);

        assertThat(foundItems, is(equalTo(expectedItems)));
    }

    @Test
    public void testFindAllByOwnerIdOrderByIdAsc_WhenUserHaveNoItems_ThenReturnEmptyList() {
        List<Item> foundItems = itemRepository.findAllByOwnerIdOrderByIdAsc(3L);

        assertThat(foundItems, is(equalTo(Collections.emptyList())));
    }

    @Test
    public void testFindAllByOwnerIdOrderByIdAsc_WhenUserHaveItemsFirstPageOfTwoElements_ThenReturnItems() {
        List<Item> expectedItems = List.of(new Item(1L, "Дрель", "Привет соседям", true, 1L, null),
                new Item(2L, "Отвертка", "Чтобы закрутить", true, 1L, null));

        List<Item> foundItems = itemRepository.findAllByOwnerIdOrderByIdAsc(1L, PageRequest.of(0, 2));

        assertThat(foundItems, is(equalTo(expectedItems)));
    }

    @Test
    public void testFindAllByOwnerIdOrderByIdAsc_WhenUserHaveItemsSecondPageOfTwoElements_ThenReturnItems() {
        List<Item> expectedItems = List.of(new Item(3L, "Гаечный ключ", "На 17-19", true, 1L, null));

        List<Item> foundItems = itemRepository.findAllByOwnerIdOrderByIdAsc(1L, PageRequest.of(1, 2));

        assertThat(foundItems, is(equalTo(expectedItems)));
    }

    @Test
    public void testFindByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableTrue() {
        List<Item> expectedItems = List.of(new Item(3L, "Гаечный ключ", "На 17-19", true, 1L, null),
                new Item(4L, "Фонарь", "Компактный фонарь", true, 2L, null),
                new Item(7L, "Ключ-трещетка", "С набором головок", true, 6L, null));

        List<Item> foundItems = itemRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableTrue("Ключ", "Фонарь",
                        PageRequest.of(0, 3));

        assertThat(foundItems, is(equalTo(expectedItems)));

        foundItems = itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableTrue(
                "Станок", "Станок", PageRequest.of(0, 3));

        assertThat(foundItems, is(equalTo(Collections.emptyList())));
    }

    @Test
    public void testFindAllByRequestIdIn_WhenItemsFound_ThenReturnItems() {
        List<Item> expectedItems = List.of(new Item(5L, "Перфоратор", "Соседи в шоке", true, 2L, 1L),
                new Item(6L, "Молоток", "С гвоздодером", true, 5L, 2L),
                new Item(10L, "Шуруповерт", "Переносной шуруповерт с аккумулятором", true, 9L, 3L));

        List<Item> foundItems = itemRepository.findAllByRequestIdIn(List.of(1L, 2L, 3L));

        assertThat(foundItems, is(equalTo(expectedItems)));
    }

    @Test
    public void testFindAllByRequestIdIn_WhenItemsNotFound_ThenReturnEmptyList() {
        List<Item> foundItems = itemRepository.findAllByRequestIdIn(List.of(10L, 11L));

        assertThat(foundItems, is(equalTo(Collections.emptyList())));
    }
}
