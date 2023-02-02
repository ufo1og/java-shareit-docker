package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.AddBookingDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto add(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                          @Validated @RequestBody AddBookingDto bookingDto) {
        return bookingService.add(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto consider(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                               @PathVariable("bookingId") Long bookingId,
                               @RequestParam("approved") Boolean approved) {
        return bookingService.consider(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable("bookingId") Long bookingId) {
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAllBookerBookings(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                   @RequestParam(required = false, defaultValue = "ALL") String state,
                                                 @RequestParam(required = false, defaultValue = "0") Integer from,
                                                 @RequestParam(required = false) Optional<Integer> size) {
        return bookingService.getAllBookerBookings(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                @RequestParam(required = false, defaultValue = "ALL") String state,
                                                @RequestParam(required = false, defaultValue = "0") Integer from,
                                                @RequestParam(required = false) Optional<Integer> size) {
        return bookingService.getAllOwnerBookings(ownerId, state, from, size);
    }
}
