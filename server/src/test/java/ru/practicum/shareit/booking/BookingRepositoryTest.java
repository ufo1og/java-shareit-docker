package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingRepositoryTest {
    private final BookingRepository bookingRepository;

    @Test
    public void testFindAllByBookerIdOrderByStartDateDesc_WhenBookingsFound_ThenReturnBookings() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdOrderByStartDateDesc(4L, PageRequest.of(0, 10));

        assertThat(bookings.size(), is(equalTo(2)));
        assertThat(bookings.get(0).getBookerId(), is(equalTo(4L)));
        assertThat(bookings.get(1).getBookerId(), is(equalTo(4L)));
    }

    @Test
    public void testFindAllByBookerIdAndEndDateBeforeOrderByStartDateDesc() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndEndDateBeforeOrderByStartDateDesc(3L,
                LocalDateTime.now(), PageRequest.of(0, 10));

        assertThat(bookings.size(), is(equalTo(1)));
        assertThat(bookings.get(0).getId(), is(equalTo(1L)));
    }

    @Test
    public void testFindAllByBookerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc() {
        List<Booking> bookings = bookingRepository
                .findAllByBookerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(3L, LocalDateTime.now(),
                        LocalDateTime.now(), PageRequest.of(0, 10));

        assertThat(bookings.size(), is(equalTo(1)));
        assertThat(bookings.get(0).getId(), is(equalTo(6L)));
    }

    @Test
    public void testFindAllByBookerIdAndStartDateAfterOrderByStartDateDesc() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStartDateAfterOrderByStartDateDesc(3L,
                LocalDateTime.now(), PageRequest.of(0, 10));

        assertThat(bookings.size(), is(equalTo(1)));
        assertThat(bookings.get(0).getId(), is(equalTo(8L)));
    }

    @Test
    public void testFindAllByBookerIdAndStatusOrderByStartDateDesc_WhenStatusWaiting() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDateDesc(3L,
                BookingStatus.WAITING, PageRequest.of(0, 10));

        assertThat(bookings.size(), is(equalTo(1)));
        assertThat(bookings.get(0).getId(), is(equalTo(8L)));
    }

    @Test
    public void testFindAllByBookerIdAndStatusOrderByStartDateDesc_WhenStatusRejected() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDateDesc(10L,
                BookingStatus.REJECTED, PageRequest.of(0, 10));

        assertThat(bookings.size(), is(equalTo(1)));
        assertThat(bookings.get(0).getId(), is(equalTo(4L)));
    }

    @Test
    public void testFindAllByItemIdInOrderByStartDateDesc() {
        List<Booking> bookings = bookingRepository.findAllByItemIdInOrderByStartDateDesc(Collections.singletonList(1L),
                PageRequest.of(0, 10));

        assertThat(bookings.size(), is(equalTo(3)));
        assertThat(bookings.get(0).getId(), is(equalTo(4L)));
        assertThat(bookings.get(1).getId(), is(equalTo(5L)));
        assertThat(bookings.get(2).getId(), is(equalTo(1L)));
    }

    @Test
    public void testFindAllByItemIdInAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc() {
        List<Booking> bookings = bookingRepository
                .findAllByItemIdInAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(Collections.singletonList(5L),
                        LocalDateTime.now(), LocalDateTime.now(), PageRequest.of(0, 10));

        assertThat(bookings.size(), is(equalTo(1)));
        assertThat(bookings.get(0).getId(), is(equalTo(6L)));
    }

    @Test
    public void testFindAllByItemIdInAndEndDateBeforeAndStatusNotOrderByStartDateDesc() {
        List<Booking> bookings = bookingRepository.findAllByItemIdInAndEndDateBeforeAndStatusNotOrderByStartDateDesc(
                List.of(1L, 2L), LocalDateTime.now(), BookingStatus.REJECTED, PageRequest.of(0, 10));

        assertThat(bookings.size(), is(equalTo(3)));
        assertThat(bookings.get(0).getId(), is(equalTo(5L)));
        assertThat(bookings.get(1).getId(), is(equalTo(2L)));
        assertThat(bookings.get(2).getId(), is(equalTo(1L)));
    }

    @Test
    public void testFindAllByItemIdInAndStartDateAfterOrderByStartDateDesc() {
        List<Booking> bookings = bookingRepository.findAllByItemIdInAndStartDateAfterOrderByStartDateDesc(
                Collections.singletonList(3L), LocalDateTime.now(), PageRequest.of(0, 10));

        assertThat(bookings.size(), is(equalTo(1)));
        assertThat(bookings.get(0).getId(), is(equalTo(3L)));
    }

    @Test
    public void testFindAllByItemIdInAndStatusOrderByStartDateDesc() {
        List<Booking> bookings = bookingRepository.findAllByItemIdInAndStatusOrderByStartDateDesc(
                List.of(3L, 10L), BookingStatus.WAITING, PageRequest.of(0, 10));

        assertThat(bookings.size(), is(equalTo(2)));
        assertThat(bookings.get(0).getId(), is(equalTo(3L)));
        assertThat(bookings.get(1).getId(), is(equalTo(8L)));
    }

    @Test
    public void testFindAllByItemIdIn() {
        List<Booking> bookings = bookingRepository.findAllByItemIdIn(List.of(1L, 2L, 3L, 5L, 6L, 10L));

        assertThat(bookings.size(), is(equalTo(8)));
    }

    @Test
    public void testFindAllByItemIdAndBookerIdAndStatusAndStartDateBefore() {
        List<Booking> bookings = bookingRepository.findAllByItemIdAndBookerIdAndStatusAndStartDateBefore(1L, 3L,
                BookingStatus.APPROVED, LocalDateTime.now());

        assertThat(bookings.size(), is(equalTo(1)));
        assertThat(bookings.get(0).getId(), is(equalTo(1L)));
    }
}
