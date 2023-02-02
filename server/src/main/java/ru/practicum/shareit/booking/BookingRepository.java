package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDateDesc(Long bookerId, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndDateBeforeOrderByStartDateDesc(Long userId,
                                                                        LocalDateTime date,
                                                                        Pageable pageable);

    List<Booking> findAllByBookerIdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(Long userId,
                                                                                         LocalDateTime startBeforeDate,
                                                                                         LocalDateTime endAfterDate,
                                                                                         Pageable pageable);

    List<Booking> findAllByBookerIdAndStartDateAfterOrderByStartDateDesc(Long userId,
                                                                         LocalDateTime date,
                                                                         Pageable pageable);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDateDesc(Long userId, BookingStatus status, Pageable pageable);

    List<Booking> findAllByItemIdInOrderByStartDateDesc(List<Long> itemIds, Pageable pageable);

    List<Booking> findAllByItemIdInAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(List<Long> itemIds,
                                                                                         LocalDateTime startBeforeDate,
                                                                                         LocalDateTime endAfterDate,
                                                                                         Pageable pageable);

    List<Booking> findAllByItemIdInAndEndDateBeforeAndStatusNotOrderByStartDateDesc(List<Long> itemIds,
                                                                                    LocalDateTime date,
                                                                                    BookingStatus status,
                                                                                    Pageable pageable);

    List<Booking> findAllByItemIdInAndStartDateAfterOrderByStartDateDesc(List<Long> itemIds,
                                                                         LocalDateTime date,
                                                                         Pageable pageable);

    List<Booking> findAllByItemIdInAndStatusOrderByStartDateDesc(List<Long> itemIds,
                                                                 BookingStatus status,
                                                                 Pageable pageable);

    List<Booking> findAllByItemIdIn(List<Long> itemIds);

    List<Booking> findAllByItemIdAndBookerIdAndStatusAndStartDateBefore(Long itemId, Long bookerId,
                                                                        BookingStatus status, LocalDateTime date);
}