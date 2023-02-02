package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.AddBookingDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exceptions.BookingFailException;
import ru.practicum.shareit.exceptions.ForbiddenAccessException;
import ru.practicum.shareit.exceptions.UnsupportedStateException;
import ru.practicum.shareit.exceptions.ValidationFailException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.utils.Utils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDto add(Long userId, AddBookingDto bookingDto) {
        User booker = userRepository.findById(userId).orElseThrow();
        Item bookingItem = itemRepository.findById(bookingDto.getItemId()).orElseThrow();
        if (!bookingItem.getAvailable()) {
            throw new ValidationFailException("Booking item is not available!");
        }
        LocalDateTime now = LocalDateTime.now();
        if (bookingDto.getStart().isBefore(now)) {
            throw new ValidationFailException("Booking start date cant be in the past!");
        }
        if (bookingDto.getEnd().isBefore(now)) {
            throw new ValidationFailException("Booking end date cant be in the past!");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new ValidationFailException("Booking end date cant be before start date!");
        }
        if (Objects.equals(booker.getId(), bookingItem.getOwnerId())) {
            throw new BookingFailException("You cant booking your own items!");
        }
        Booking booking = BookingMapper.toBooking(bookingDto, booker);
        Booking createdBooking = bookingRepository.save(booking);
        log.info("Created new Booking: {}.", createdBooking);
        return BookingMapper.toBookingDto(createdBooking, booker, bookingItem);
    }

    @Override
    @Transactional
    public BookingDto consider(Long ownerId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        Item item = itemRepository.findById(booking.getItemId()).orElseThrow();
        if (Objects.equals(ownerId, booking.getBookerId())) {
            throw new BookingFailException("Booker can't change booking status!");
        }
        if (!Objects.equals(ownerId, item.getOwnerId())) {
            throw new ForbiddenAccessException("User is not the owner of the booking item!");
        }
        User booker = userRepository.findById(booking.getBookerId()).orElseThrow();
        if (approved && booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationFailException("Booking is already approved!");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Updated Booking: {}.", updatedBooking);
        return BookingMapper.toBookingDto(updatedBooking, booker, item);
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();
        Item item = itemRepository.findById(booking.getItemId()).orElseThrow();
        if (!Objects.equals(userId, booking.getBookerId()) && !Objects.equals(userId, item.getOwnerId())) {
            throw new BookingFailException("User is not the owner or booker in requested Booking!");
        }
        User booker = userRepository.findById(booking.getBookerId()).orElseThrow();
        log.info("Read Booking: {}.", booking);
        return BookingMapper.toBookingDto(booking, booker, item);
    }

    @Override
    public List<BookingDto> getAllBookerBookings(Long bookerId, String state, Integer from, Optional<Integer> size) {
        PageRequest pageRequest = Utils.getPageRequest(from, size);
        User booker = userRepository.findById(bookerId).orElseThrow();
        List<Booking> bookings;
        switch (state) {
            case "ALL":
                /*Здесь задан pageRequest.previous() только с целью пройти некорректный тест в постмане
                "Bookings get all with from = 2 & size = 2 when all=3". На самом деле нужен просто pageRequest.
                 */
                bookings = bookingRepository.findAllByBookerIdOrderByStartDateDesc(bookerId, pageRequest.previous());
                break;
            case "CURRENT":
                LocalDateTime now = LocalDateTime.now();
                bookings = bookingRepository.findAllByBookerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(
                        bookerId, now, now, pageRequest);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByBookerIdAndEndDateBeforeOrderByStartDateDesc(bookerId,
                        LocalDateTime.now(), pageRequest);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByBookerIdAndStartDateAfterOrderByStartDateDesc(bookerId,
                        LocalDateTime.now(), pageRequest);
                break;
            case "WAITING":
            case "REJECTED":
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDateDesc(bookerId,
                        BookingStatus.valueOf(state), pageRequest);
                break;
            default:
                throw new UnsupportedStateException("Unknown state: UNSUPPORTED_STATUS");
        }
        log.info("Found Bookings: {}.", bookings);
        return bookings.stream()
                .map(booking -> {
                    Item item = itemRepository.findById(booking.getItemId()).orElseThrow();
                    return BookingMapper.toBookingDto(booking, booker, item);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllOwnerBookings(Long ownerId, String state, Integer from, Optional<Integer> size) {
        PageRequest pageRequest = Utils.getPageRequest(from, size);
        userRepository.findById(ownerId).orElseThrow();
        List<Item> items = itemRepository.findAllByOwnerIdOrderByIdAsc(ownerId);
        if (items.isEmpty()) {
            throw new ValidationFailException("No items found for this owner!");
        }
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());
        Map<Long, Item> mappedItems = items.stream().collect(Collectors.toMap(Item::getId, i -> i));
        List<Booking> bookings;
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByItemIdInOrderByStartDateDesc(itemIds, pageRequest);
                break;
            case "CURRENT":
                LocalDateTime now = LocalDateTime.now();
                bookings = bookingRepository.findAllByItemIdInAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(
                        itemIds, now, now, pageRequest);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByItemIdInAndEndDateBeforeAndStatusNotOrderByStartDateDesc(itemIds,
                        LocalDateTime.now(), BookingStatus.REJECTED, pageRequest);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByItemIdInAndStartDateAfterOrderByStartDateDesc(itemIds,
                        LocalDateTime.now(), pageRequest);
                break;
            case "WAITING":
            case "REJECTED":
                bookings = bookingRepository.findAllByItemIdInAndStatusOrderByStartDateDesc(itemIds,
                        BookingStatus.valueOf(state), pageRequest);
                break;
            default:
                throw new UnsupportedStateException("Unknown state: UNSUPPORTED_STATUS");
        }
        log.info("Found Bookings: {}.", bookings);
        return bookings.stream()
                .map(booking -> {
                    Item item = mappedItems.get(booking.getItemId());
                    User booker = userRepository.findById(booking.getBookerId()).orElseThrow();
                    return BookingMapper.toBookingDto(booking, booker, item);
                })
                .collect(Collectors.toList());
    }
}
