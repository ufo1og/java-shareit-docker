package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exceptions.ForbiddenAccessException;
import ru.practicum.shareit.exceptions.ValidationFailException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.utils.Utils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    @Override
    public ItemDto add(long userId, ItemDto itemDto) {
        User owner = userRepository.findById(userId).orElseThrow();
        Item item = ItemMapper.toItem(itemDto, owner.getId());
        Item addedItem = itemRepository.save(item);
        log.info("Added new Item: {}.", addedItem);
        return ItemMapper.toItemDto(addedItem, null, null, null);
    }

    @Transactional
    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        Item itemToUpdate = itemRepository.findById(itemId).orElseThrow();
        if (itemToUpdate.getOwnerId() != userId) {
            throw new ForbiddenAccessException(String.format("User with id %s is not the owner!", userId));
        }
        Item item = ItemMapper.toItem(itemDto, userId);
        Optional.ofNullable(item.getName()).ifPresent(name -> {
            if (!name.isBlank()) {
                itemToUpdate.setName(name);
            }
        });
        Optional.ofNullable(item.getDescription()).ifPresent(description -> {
            if (!description.isBlank()) {
                itemToUpdate.setDescription(description);
            }
        });
        Optional.ofNullable(item.getAvailable()).ifPresent(itemToUpdate::setAvailable);
        Item updatedItem = itemRepository.save(itemToUpdate);
        log.info("Updated Item: {}.", updatedItem);
        return ItemMapper.toItemDto(updatedItem, null, null, null);
    }

    @Override
    public ItemDto getById(long userId, long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow();
        List<CommentDto> comments = commentRepository.findAllByItemIdIn(List.of(item.getId()))
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        ItemDto itemDto;
        if (Objects.equals(item.getOwnerId(), userId)) {
            List<Booking> bookings = bookingRepository.findAllByItemIdIn(List.of(item.getId()));
            BookingInfo bookingInfo = findLastAndNextBooking(bookings, List.of(itemId)).get(itemId);
            itemDto = ItemMapper.toItemDto(item, bookingInfo.getLastBooking(), bookingInfo.getNextBooking(), comments);
        } else {
            itemDto = ItemMapper.toItemDto(item, null, null, comments);
        }
        log.info("Read Item: {}.", item);
        return itemDto;
    }

    @Override
    public List<ItemDto> getByUser(long userId, Integer from, Optional<Integer> size) {
        PageRequest pageRequest = Utils.getPageRequest(from, size);
        List<Item> readItems = itemRepository.findAllByOwnerIdOrderByIdAsc(userId, pageRequest);
        List<Long> itemIds = readItems.stream().map(Item::getId).collect(Collectors.toList());
        List<Booking> bookings = bookingRepository.findAllByItemIdIn(itemIds);
        List<Comment> comments = commentRepository.findAllByItemIdIn(itemIds);
        Map<Long, List<CommentDto>> itemComments = new HashMap<>();
        for (Comment comment : comments) {
            Long itemId = comment.getItemId();
            CommentDto commentDto = CommentMapper.toCommentDto(comment);
            if (itemComments.containsKey(itemId)) {
                itemComments.get(itemId).add(commentDto);
            } else {
                itemComments.put(itemId, List.of(commentDto));
            }
        }
        Map<Long, BookingInfo> itemBookings = findLastAndNextBooking(bookings, itemIds);
        log.info("Read Items: {}.", readItems);
        return readItems.stream()
                .map(item -> {
                    Booking lastBooking = itemBookings.get(item.getId()).getLastBooking();
                    Booking nextBooking = itemBookings.get(item.getId()).getNextBooking();
                    List<CommentDto> commentDtos = itemComments.get(item.getId());
                    return ItemMapper.toItemDto(item, lastBooking, nextBooking, commentDtos);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text, Integer from, Optional<Integer> size) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        PageRequest pageRequest = Utils.getPageRequest(from, size);
        List<Item> foundItems = itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableTrue(text, text,
                pageRequest);
        log.info("Found Items: {}.", foundItems);
        return foundItems.stream()
                .map(item -> ItemMapper.toItemDto(item, null, null, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentCreateDto commentDto) {
        User user = userRepository.findById(userId).orElseThrow();
        List<Booking> bookings = bookingRepository.findAllByItemIdAndBookerIdAndStatusAndStartDateBefore(itemId, userId,
                BookingStatus.APPROVED, LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new ValidationFailException(String.format("User with id = %s doesn't use item with id = %s!",
                    userId, itemId));
        }
        Comment createdComment = commentRepository.save(CommentMapper.toComment(commentDto, user, itemId));
        log.info("Added new Comment: {}.", createdComment);
        return CommentMapper.toCommentDto(createdComment);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    private static class BookingInfo {
        private Booking lastBooking;
        private Booking nextBooking;
    }

    private Map<Long, BookingInfo> findLastAndNextBooking(List<Booking> bookings, List<Long> itemIds) {
        LocalDateTime now = LocalDateTime.now();
        Map<Long, BookingInfo> result = itemIds.stream().collect(Collectors.toMap(l -> l, l -> new BookingInfo()));
        for (Booking booking : bookings) {
            Long itemId = booking.getItemId();
            BookingInfo bookingInfo = result.get(itemId);

            if (booking.getEndDate().isBefore(now) || booking.getEndDate().isEqual(now)) {
                LocalDateTime endDate = booking.getEndDate();
                if (bookingInfo.getLastBooking() == null) {
                    bookingInfo.setLastBooking(booking);
                } else if (endDate.isAfter(bookingInfo.getLastBooking().getEndDate())) {
                    bookingInfo.setLastBooking(booking);
                }
            }

            if (booking.getStartDate().isAfter(now)) {
                LocalDateTime startDate = booking.getStartDate();
                if (bookingInfo.getNextBooking() == null) {
                    bookingInfo.setNextBooking(booking);
                } else if (startDate.isBefore(bookingInfo.getNextBooking().getStartDate())) {
                    bookingInfo.setNextBooking(booking);
                }
            }
        }
        return result;
    }
}
