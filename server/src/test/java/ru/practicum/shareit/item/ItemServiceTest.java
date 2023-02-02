package ru.practicum.shareit.item;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.exceptions.ForbiddenAccessException;
import ru.practicum.shareit.exceptions.ValidationFailException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;

    private ItemService itemService;

    @BeforeEach
    public void setItemService() {
        this.itemService = new ItemServiceImpl(commentRepository, itemRepository, userRepository, bookingRepository);
    }

    @Test
    public void testAdd_WhenUserNotExists_ThenThrow() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        NoSuchElementException e = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> itemService.add(1L, new ItemDto(null, "item", "good item", true, null, null, null, null))
        );

        assertThat(e.getMessage(), is(equalTo("No value present")));
    }

    @Test
    public void testAdd_WhenUserExists_ThenOK() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(new User(1L, "John", "john@mail.ru")));
        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(new Item());

        itemService.add(1L, new ItemDto(null, "item", "good item", true, null, null, null, null));

        Item expectedItem = new Item(null, "item", "good item", true, 1L, null);
        Mockito.verify(itemRepository, Mockito.times(1)).save(expectedItem);
    }

    @Test
    public void testUpdate_WhenItemNotFound_ThenThrow() {
        Mockito.when(itemRepository.findById(1L))
                .thenReturn(Optional.empty());

        NoSuchElementException e = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> itemService.update(1L, 1L, null)
        );

        assertThat(e.getMessage(), is(equalTo("No value present")));
    }

    @Test
    public void testUpdate_WhenUserNotMatch_ThenThrow() {
        Mockito.when(itemRepository.findById(1L))
                .thenReturn(Optional.of(new Item(1L, null, null, true, 100L, null)));

        ForbiddenAccessException e = Assertions.assertThrows(
                ForbiddenAccessException.class,
                () -> itemService.update(1L, 1L, null)
        );

        assertThat(e.getMessage(), is(equalTo("User with id 1 is not the owner!")));
    }

    @Test
    public void testUpdate_UpdateOnlyName_ThenOK() {
        Item item = new Item(1L, "item", "good item", true, 1L, null);

        Mockito.when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(new Item());

        ItemDto itemDto = new ItemDto(1L, "new item", null, null, null, null, null, null);

        itemService.update(1L, 1L, itemDto);
        item.setName("new item");

        Mockito.verify(itemRepository, Mockito.times(1)).save(item);
    }

    @Test
    public void testUpdate_UpdateOnlyDescription_ThenOK() {
        Item item = new Item(1L, "item", "good item", true, 1L, null);

        Mockito.when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(new Item());

        ItemDto itemDto = new ItemDto(1L, null, "best item", null, null, null, null, null);

        itemService.update(1L, 1L, itemDto);
        item.setDescription("best item");

        Mockito.verify(itemRepository, Mockito.times(1)).save(item);
    }

    @Test
    public void testUpdate_UpdateOnlyAvailable_ThenOK() {
        Item item = new Item(1L, "item", "good item", true, 1L, null);

        Mockito.when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(new Item());

        ItemDto itemDto = new ItemDto(1L, null, null, false, null, null, null, null);

        itemService.update(1L, 1L, itemDto);
        item.setAvailable(false);

        Mockito.verify(itemRepository, Mockito.times(1)).save(item);
    }

    @Test
    public void testUpdate_UpdateNameAndDescriptionAndAvailable_ThenOK() {
        Item item = new Item(1L, "item", "good item", true, 1L, null);

        Mockito.when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(new Item());

        ItemDto itemDto = new ItemDto(1L, "new item", "best item", false, null, null, null, null);

        itemService.update(1L, 1L, itemDto);
        item.setName("new item");
        item.setDescription("best item");
        item.setAvailable(false);

        Mockito.verify(itemRepository, Mockito.times(1)).save(item);
    }

    @Test
    public void testGetById_WhenItemNotExists_ThenThrow() {
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        NoSuchElementException e = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> itemService.getById(1L, 1L)
        );

        assertThat(e.getMessage(), is(equalTo("No value present")));
    }

    @Test
    public void testGetById_WhenUserNotOwner_ThenOK() {
        List<Comment> comments = List.of(new Comment(1L, 1L, "really good item", "Sam",
                LocalDateTime.now().minusYears(1)));
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Item(1L, "item", "good item", true, 100L, null)));
        Mockito.when(commentRepository.findAllByItemIdIn(Mockito.anyList()))
                .thenReturn(comments);

        ItemDto itemDto = itemService.getById(1L, 1L);

        assertThat(itemDto.getName(), is(equalTo("item")));
        assertThat(itemDto.getDescription(), is(equalTo("good item")));
        assertThat(itemDto.getAvailable(), is(true));
        assertThat(itemDto.getLastBooking(), is(nullValue()));
        assertThat(itemDto.getNextBooking(), is(nullValue()));
        assertThat(itemDto.getComments().size(), is(equalTo(1)));
        assertThat(itemDto.getComments().get(0).getText(), is(equalTo("really good item")));
    }

    @Test
    public void testGetById_WhenUSerIsOwner_ThenOK() {
        List<Comment> comments = List.of(new Comment(1L, 1L, "really good item", "Sam",
                LocalDateTime.now().minusYears(1)));

        Booking lastBooking = new Booking(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().minusHours(1), 1L,
                10L, BookingStatus.APPROVED);
        Booking nextBooking = new Booking(2L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), 1L,
                20L, BookingStatus.APPROVED);
        List<Booking> bookings = List.of(lastBooking, nextBooking);

        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new Item(1L, "item", "good item", true, 1L, null)));
        Mockito.when(commentRepository.findAllByItemIdIn(Mockito.anyList()))
                .thenReturn(comments);
        Mockito.when(bookingRepository.findAllByItemIdIn(Mockito.anyList()))
                .thenReturn(bookings);

        ItemDto itemDto = itemService.getById(1L, 1L);

        assertThat(itemDto.getName(), is(equalTo("item")));
        assertThat(itemDto.getDescription(), is(equalTo("good item")));
        assertThat(itemDto.getAvailable(), is(true));
        assertThat(itemDto.getLastBooking(), is(equalTo(new BookingInfoDto(lastBooking))));
        assertThat(itemDto.getNextBooking(), is(equalTo(new BookingInfoDto(nextBooking))));
        assertThat(itemDto.getComments().size(), is(equalTo(1)));
        assertThat(itemDto.getComments().get(0).getText(), is(equalTo("really good item")));
    }

    @Test
    public void testGetByUser_StandardBehaviour_ThenOK() {
        Item firstItem = new Item(1L, "item1", "item1_desc", true, 1L, null);
        Item secondItem = new Item(2L, "item1", "item1_desc", true, 1L, null);
        List<Item> items = List.of(firstItem, secondItem);
        Mockito.when(itemRepository.findAllByOwnerIdOrderByIdAsc(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(items);

        Booking firstItemLastBooking = new Booking(1L, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), 1L, 2L, BookingStatus.APPROVED);
        Booking firstItemNextBooking = new Booking(2L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), 1L, 3L, BookingStatus.APPROVED);
        Booking secondItemLastBooking = new Booking(3L, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), 2L, 4L, BookingStatus.APPROVED);
        Booking secondItemNextBooking = new Booking(4L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), 2L, 5L, BookingStatus.APPROVED);
        List<Booking> bookings = List.of(firstItemLastBooking, firstItemNextBooking, secondItemLastBooking,
                secondItemNextBooking);
        Mockito.when(bookingRepository.findAllByItemIdIn(Mockito.anyList()))
                .thenReturn(bookings);

        Comment firstItemComment = new Comment(1L, 1L, "good item", "Sam", LocalDateTime.now().minusDays(1));
        Comment secondItemComment = new Comment(2L, 2L, "best item", "John", LocalDateTime.now().minusDays(1));
        List<Comment> comments = List.of(firstItemComment, secondItemComment);
        Mockito.when(commentRepository.findAllByItemIdIn(Mockito.anyList()))
                .thenReturn(comments);

        List<ItemDto> foundItems = itemService.getByUser(1L, 0, Optional.of(5));

        assertThat(foundItems.size(), is(equalTo(2)));
        assertThat(foundItems.get(0).getComments().size(), is(equalTo(1)));
        assertThat(foundItems.get(1).getComments().size(), is(equalTo(1)));
        assertThat(foundItems.get(0).getLastBooking().getId(), is(equalTo(1L)));
        assertThat(foundItems.get(0).getNextBooking().getId(), is(equalTo(2L)));
        assertThat(foundItems.get(1).getLastBooking().getId(), is(equalTo(3L)));
        assertThat(foundItems.get(1).getNextBooking().getId(), is(equalTo(4L)));
    }

    @Test
    public void testSearchItems_WhenTextIsEmpty_ThenReturnEmptyList() {
        List<ItemDto> foundItems = itemService.searchItems("", 0, Optional.of(10));

        assertThat(foundItems.size(), is(equalTo(0)));
    }

    @Test
    public void testSearchItems_StandardBehaviour_ThenOK() {
        Mockito.when(itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableTrue(Mockito.anyString(),
                Mockito.anyString(), Mockito.any(Pageable.class))).thenReturn(Collections.emptyList());

        List<ItemDto> foundItems = itemService.searchItems("item", 0, Optional.of(10));

        assertThat(foundItems.size(), is(equalTo(0)));
    }

    @Test
    public void testAddComment_UserNotExists_ThenThrow() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        NoSuchElementException e = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> itemService.addComment(1L, 1L, new CommentCreateDto("good item"))
        );

        assertThat(e.getMessage(), is(equalTo("No value present")));
    }

    @Test
    public void testAddComment_UserDontUseItem_ThenThrow() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User(1L, "John", "john@ya.ru")));
        Mockito.when(bookingRepository.findAllByItemIdAndBookerIdAndStatusAndStartDateBefore(Mockito.anyLong(),
                Mockito.anyLong(), Mockito.any(BookingStatus.class), Mockito.any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        ValidationFailException e = Assertions.assertThrows(
                ValidationFailException.class,
                () -> itemService.addComment(1L, 1L, new CommentCreateDto("good item"))
        );

        assertThat(e.getMessage(), is(equalTo("User with id = 1 doesn't use item with id = 1!")));
    }

    @Test
    public void testAddComment_StandardBehaviour_ThenOK() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(new User(1L, "John", "john@ya.ru")));
        Mockito.when(bookingRepository.findAllByItemIdAndBookerIdAndStatusAndStartDateBefore(Mockito.anyLong(),
                        Mockito.anyLong(), Mockito.any(BookingStatus.class), Mockito.any(LocalDateTime.class)))
                .thenReturn(List.of(new Booking(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1),
                        1L, 1L, BookingStatus.APPROVED)));
        Mockito.when(commentRepository.save(Mockito.any(Comment.class)))
                        .thenReturn(new Comment());

        itemService.addComment(1L, 1L, new CommentCreateDto("good item"));

        Mockito.verify(commentRepository, Mockito.times(1)).save(Mockito.any(Comment.class));
    }
}
