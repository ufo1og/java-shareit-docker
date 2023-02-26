package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") long userId,
						  @Valid @RequestBody BookItemRequestDto bookingDto) {
		log.info("Creating booking {}, userId={}", bookingDto, userId);
		return bookingClient.add(userId, bookingDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> consider(@RequestHeader("X-Sharer-User-Id") long userId,
							   @PathVariable("bookingId") long bookingId,
							   @RequestParam("approved") Boolean approved) {
		log.info("Considering booking, bookingId={}, approved={}, userId={}", bookingId, approved, userId);
		return bookingClient.consider(userId, bookingId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") long userId,
							  @PathVariable("bookingId") long bookingId) {
		log.info("Getting booking, userId={}, bookingId={}", userId, bookingId);
		return bookingClient.getBookingById(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> getAllBookerBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
												 @RequestParam(required = false, defaultValue = "ALL") String state,
												 @RequestParam(required = false, defaultValue = "0") Integer from,
												 @RequestParam(required = false) Integer size) {
		BookingState bookingState = BookingState.from(state)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
		log.info("Getting bookings with state {}, userId={}, from={}, size={}", state, userId, from, size);
		return bookingClient.getAllBookerBookings(userId, bookingState, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getAllOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
												@RequestParam(required = false, defaultValue = "ALL") String state,
												@RequestParam(required = false, defaultValue = "0") Integer from,
												@RequestParam(required = false) Integer size) {
		BookingState bookingState = BookingState.from(state)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
		log.info("Getting owner bookings with state {}, userId={}, from={}, size={}", state, userId, from, size);
		return bookingClient.getAllOwnerBookings(userId, bookingState, from, size);
	}
}
